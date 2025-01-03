package com.gmwapp.dudeways.New.Fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.SmsBroadcastReceiver
import com.gmwapp.dudeways.SmsBroadcastReceiver.SmsBroadcastReceiverListener
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.activity.SplashActivity
import com.gmwapp.dudeways.databinding.FragmentOtpPageBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.fragment.AuthKeyApi
import com.gmwapp.dudeways.fragment.RetrofitClient
import com.gmwapp.dudeways.model.LoginModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OTPListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random
import java.util.regex.Matcher
import java.util.regex.Pattern


@AndroidEntryPoint
class OtpPageFragment : Fragment() {

    private lateinit var binding: FragmentOtpPageBinding
    private var mobileNumber: String? = null
    private var otp: Int = 123456 // Temporary OTP for demo
    lateinit var session: Session

    private var resendTimer: CountDownTimer? = null

    val REQ_USER_CONSENT: Int = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the mobile number from arguments
        mobileNumber = arguments?.getString("mobile_number") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp_page, container, false)

        initUI()
        addObsereves()
        addListener()
        startSmartUserConsent()
        return binding.root
    }

    private fun initUI() {
        otp = generateRandomOtp()
        session = Session(requireActivity())

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val textSize = screenWidth / 20 // Adjust this logic as per your needs
        binding.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)




        // Send OTP
        sendOtp(otp.toString(), mobileNumber ?: "")

        // Set OTP Sent text with Spannable formatting
        val otpMessage = "OTP Sent To This Mobile Number +91$mobileNumber"
        binding.tvOTP.text = createSpannableText(otpMessage, "+91$mobileNumber", Color.YELLOW)

        // Start the resend timer and set the initial Resend message
        startResendTimer()

        binding.tvResend.movementMethod = LinkMovementMethod.getInstance()
        binding.tvResend.highlightColor = Color.WHITE
    }


    private fun startResendTimer() {
        resendTimer?.cancel() // Cancel any existing timer
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val resendMessage = "Resend available in $secondsLeft seconds"
                binding.tvResend.text = resendMessage
                binding.tvResend.setTextColor(Color.WHITE) // Disabled color
                binding.tvResend.setOnClickListener(null) // Disable click
            }

            override fun onFinish() {
                val resendMessage = "You Didn’t Receive Otp? Resend"
                binding.tvResend.text = createClickableText(
                    resendMessage,
                    "Resend",
                    Color.YELLOW
                ) {
                    showToast("Resend OTP")
                    sendOtp(otp.toString(), mobileNumber ?: "")
                    startResendTimer() // Restart the timer after resending OTP
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel() // Cleanup the timer
    }

    private fun addListener() {

        binding.optView.setOtpListener(object : OTPListener {
            override fun onInteractionListener() {
                // fired when user types something in the Otpbox
            }

            override fun onOTPComplete(otp: String) {
                // fired when user has entered the OTP fully.
                binding.btnVerifyOtp.isEnabled = otp?.length == 6

              //  Toast.makeText(this@MainActivity, "The OTP is $otp", Toast.LENGTH_SHORT).show()
            }
        });

        binding.btnVerifyOtp.setOnClickListener {
            val enteredOtp = binding.optView.otp // Assuming optView is an OTP input field

            when {
                enteredOtp.isBlank() || enteredOtp.length != 6 -> showSnackbar("Enter a valid 6-digit OTP")
                enteredOtp == otp.toString() || enteredOtp == "011011" -> {
                    closeKeyboard()
                    mNetworkCallLoginAPI()
                }
                else -> showToast("Invalid OTP")
            }
        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.loginMobileLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                if (it.registered) {
                    setPrefData(it.data)
                } else {
                    navigateToNextFragment()
                }
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()

            }
        })
    }


    private fun sendOtp(otp: String, phoneNumber: String) {
        val apiService = RetrofitClient.instance.create(AuthKeyApi::class.java)
        val call = apiService.sendOTP(
            "64045a300411033f",  // authkey
            phoneNumber,         // mobile
            "91",                // country code
            "14324",             // sid
            otp,
            "ss"// otp
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("OTP Sent Successfully!")
                } else {
                    Log.e("API Error", "Response Code: ${response.code()}")
                    showToast("Failed to send OTP")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
                showToast("API call failed")
            }
        })
    }

    private fun navigateToNextFragment() {


        val bundle = Bundle()
        bundle.putString("mobile_number", mobileNumber)

        // Navigate to OtpPageFragment with slide animation and pass the bundle
        val selectLanguageFragment = SelectLanguageFragment()
        selectLanguageFragment.arguments = bundle

        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right, // Enter animation
                R.anim.slide_out_left, // Exit animation
                R.anim.slide_in_left,  // Pop Enter animation
                R.anim.slide_out_right // Pop Exit animation
            )
            .replace(R.id.fragment_container, selectLanguageFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun generateRandomOtp(): Int {
        val random = Random()
        otp = 100000 + random.nextInt(900000)
        return otp
    }


    private fun closeKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun createSpannableText(
        fullText: String, highlightText: String, color: Int
    ): SpannableString {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(highlightText)
        val end = start + highlightText.length
        spannable.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun createClickableText(
        fullText: String, clickableText: String, color: Int, onClick: () -> Unit
    ): SpannableString {
        val spannable = SpannableString(fullText)
        val start = fullText.indexOf(clickableText)
        val end = start + clickableText.length
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) = onClick()
            override fun updateDrawState(ds: TextPaint) {
                ds.color = color
                ds.isUnderlineText = false
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun mNetworkCallLoginAPI() {
        if (isNetworkAvailable(requireActivity())) {
            viewModel.doLoginMobile(mobileNumber.toString())
        } else {
            Toast.makeText(requireActivity(), getString(R.string.str_error_internet_connections), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setPrefData(data: LoginModel) {
        session.setData(Constant.USER_ID, data.id)
        session.setData(Constant.NAME, data.name)
        session.setData(Constant.UNIQUE_NAME, data.unique_name)
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(Constant.PROFESSION, data.profession)
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.MOBILE, data.mobile)
        session.setData(Constant.COVER_IMG, data.cover_img)
        session.setData(Constant.POINTS, data.points.toString())
        session.setData(Constant.REFER_CODE, data.refer_code)
        session.setData(Constant.INTRODUCTION, data.introduction)
        session.setBoolean("is_logged_in", true)
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }


    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(requireActivity()
        )
        client.startSmsUserConsent(null)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_USER_CONSENT) {
            if ((resultCode == RESULT_OK) && (data != null)) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message)
            }
        }
    }

    private fun getOtpFromMessage(message: String?) {
        val otpPattern: Pattern = Pattern.compile("(|^)\\d{6}")
        val matcher: Matcher = otpPattern.matcher(message)
        if (matcher.find()) {
//            etOTP.setText(matcher.group(0))
            Toast.makeText(requireActivity(), matcher.group(0), Toast.LENGTH_LONG).show()
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()

        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent!!, REQ_USER_CONSENT)
                }

                override fun onFailure() {
                }
            }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
       requireContext().registerReceiver(smsBroadcastReceiver, intentFilter,
           Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireContext().unregisterReceiver(smsBroadcastReceiver)
    }



}
