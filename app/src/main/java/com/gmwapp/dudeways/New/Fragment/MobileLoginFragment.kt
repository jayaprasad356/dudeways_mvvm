package com.gmwapp.dudeways.New.Fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentLoginpageBinding
import com.gmwapp.dudeways.databinding.FragmentMobileLoginBinding
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.CredentialsApi
import com.google.android.gms.auth.api.credentials.CredentialsOptions
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.material.snackbar.Snackbar


class MobileLoginFragment : Fragment() {

    lateinit var binding: FragmentMobileLoginBinding
    private lateinit var activity: Activity

    companion object {
        var CREDENTIAL_PICKER_REQUEST = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mobile_login, container, false)
        initUI()
        addListener()
//        addObservers()
        phoneSelection()
        return binding.root

    }

    private fun initUI() {
        binding.edMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Enable button only if the input is exactly 10 digits
                binding.btnSendOtp.isEnabled = s?.length == 10
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })    }

    private fun addListener() {
        binding.btnSendOtp.setOnClickListener {
            val mobileNumber = binding.edMobile.text.toString().trim()

            // Validation for mobile number
            when {
                mobileNumber.isEmpty() -> {
                    showSnackbar("Mobile number is required")
                }
                !mobileNumber.matches(Regex("^[6-9]\\d{9}$")) -> {
                    showSnackbar("Enter a valid 10-digit mobile number")
                }
                else -> {
                    closeKeyboard()
                    // Create a Bundle to pass data to the next fragment
                    val bundle = Bundle()
                    bundle.putString("mobile_number", mobileNumber)

                    // Navigate to OtpPageFragment with slide animation and pass the bundle
                    val otpPageFragment = OtpPageFragment()
                    otpPageFragment.arguments = bundle

                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.slide_in_right, // Enter animation
                            R.anim.slide_out_left, // Exit animation
                            R.anim.slide_in_left,  // Pop Enter animation (when coming back)
                            R.anim.slide_out_right // Pop Exit animation (when going back)
                        )
                        .replace(R.id.fragment_container, otpPageFragment)
                        .addToBackStack(null) // Add to back stack
                        .commit()
                }
            }
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun closeKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusedView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun phoneSelection() {
        // To retrieve the Phone Number hints, first, configure
        // the hint selector dialog by creating a HintRequest object.
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .build()

        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()

        // Then, pass the HintRequest object to
        // credentialsClient.getHintPickerIntent()
        // to get an intent to prompt the user to
        // choose a phone number.
        val credentialsClient = Credentials.getClient(requireContext(), options)
        val intent = credentialsClient.getHintPickerIntent(hintRequest)
        try {
            startIntentSenderForResult(
                intent.intentSender,
                CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0, Bundle()
            )
        } catch (e: IntentSender.SendIntentException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK) {
            // Get data from the dialog which is of type Credential
            val credential: Credential? = data?.getParcelableExtra(Credential.EXTRA_KEY)

            // Extract the mobile number without the country code
            credential?.apply {
                val fullPhoneNumber = credential.id // Example: "+919876543210"
                val mobileNumber = if (fullPhoneNumber.startsWith("+91")) {
                    fullPhoneNumber.substring(3) // Remove "+91"
                } else {
                    fullPhoneNumber // Use as is if no "+91" prefix
                }

                // Set the mobile number to the EditText
                binding.edMobile.setText(mobileNumber)
            }
        } else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE) {
            Toast.makeText(requireActivity(), "No phone numbers found", Toast.LENGTH_LONG).show()
        }
    }



}