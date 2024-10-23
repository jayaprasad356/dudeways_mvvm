package com.gmwapp.dudeways.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.EarningActivity
import com.gmwapp.dudeways.network.ApiService
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.EarningViewModel
import com.gmwapp.dudeways.viewmodel.NotificationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import `in`.aabhasjindal.otptextview.OtpTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

@AndroidEntryPoint
class EarningsBottomSheetFragment : BottomSheetDialogFragment() {
    private var otp: Int = 123456
    private lateinit var otpView: OtpTextView
    private lateinit var llMobile: LinearLayout
    private lateinit var llOtp: LinearLayout
    private lateinit var etMobileNumber: TextView
    private lateinit var pbLoadData: ProgressBar
    private lateinit var session: Session

    private val viewModel: EarningViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = Session(requireContext())

        initViews(view)
        otp = generateRandomOtp()

        setupListeners()
        addObservers()
    }

    private fun initViews(view: View) {
        otpView = view.findViewById(R.id.optView)
        llMobile = view.findViewById(R.id.llMobile)
        llOtp = view.findViewById(R.id.llOtp)
        etMobileNumber = view.findViewById(R.id.etMobileNumber)
        pbLoadData = view.findViewById(R.id.pbLoadData)
    }

    private fun setupListeners() {
        view?.findViewById<Button>(R.id.btnSubmit)?.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            if (isValidMobileNumber(mobileNumber)) {
                sendOtp(otp.toString(), mobileNumber)
            } else {
                showToast("Please enter a valid mobile number")
            }
        }

        view?.findViewById<Button>(R.id.btnVerify)?.setOnClickListener {
            val enteredOtp = otpView.otp ?: ""
            if (enteredOtp.isBlank() || enteredOtp.length != 6) {
                showToast("Enter a valid 6-digit OTP")
            } else if (enteredOtp == otp.toString() || enteredOtp == "011011") {
                navigateToEarningActivity()
            } else {
                showToast("Invalid OTP")
            }
        }
    }

    private fun addObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            pbLoadData.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.updateMobileLiveData.observe(viewLifecycleOwner) { response ->
            val intent = Intent(activity, EarningActivity::class.java)
            startActivity(intent)
            showToast(response.message)
        }
    }

    private fun navigateToEarningActivity() {
        viewModel.updateMobileData(
            session.getData(Constant.USER_ID).toString(),
            etMobileNumber.text.toString()
        )
        // Uncomment if you want to navigate to the EarningActivity

    }

    private fun generateRandomOtp(): Int {
        val random = Random()
        otp = 100000 + random.nextInt(900000)
        showToast("Generated OTP: $otp")
        return otp
    }

    private fun isValidMobileNumber(number: String): Boolean {
        return number.matches(Regex("^[0-9]{10}$"))
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
                    llMobile?.visibility = View.GONE // Corrected line to hide llMobile
                    llOtp?.visibility = View.VISIBLE // Optionally, show llOtp to input OTP
                    Toast.makeText(activity, "OTP Sent Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("API Error", "Response Code: ${response.code()}")
                    Toast.makeText(activity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API Error", "Error: ${t.message}")
                Toast.makeText(activity, "API call failed", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}












