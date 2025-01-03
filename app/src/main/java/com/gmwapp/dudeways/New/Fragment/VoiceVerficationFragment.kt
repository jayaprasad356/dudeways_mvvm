package com.gmwapp.dudeways.New.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gmwapp.dudeways.New.HomeActivity
import com.gmwapp.dudeways.New.LoginActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentVoiceVerficationBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class VoiceVerficationFragment : Fragment() {

    private lateinit var binding: FragmentVoiceVerficationBinding
    private lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voice_verfication, container, false)
        session = Session(requireContext())

        setupUI()
        handleBackPress()
        addListener()
        return binding.root
    }

    private fun setupUI() {
        binding.btnExplore.visibility = View.GONE
        binding.btnRetry.visibility = View.GONE
        val voiceVerificationStatus = session.getData(Constant.VOICE_VERIFICATION_STATUS)

        when (voiceVerificationStatus) {
            "0" -> {
                binding.ivVerifyStatus.background = resources.getDrawable(R.drawable.pending_im)
                binding.tvDescription.text = "Please wait.....\nwithin 24 hours it will be verified"
            }
            "2" -> {
                binding.ivVerifyStatus.background = resources.getDrawable(R.drawable.rejected_img)
                binding.tvDescription.text = "Sorry your voice verification is rejected"
                binding.btnRetry.visibility = View.VISIBLE
            }
            "1" -> {
                binding.ivVerifyStatus.background = resources.getDrawable(R.drawable.verified_img)
                binding.tvDescription.text = "Voice Verification Successful"
                binding.btnExplore.visibility = View.VISIBLE
            }
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish() // Close the app
            }
        })
    }

    private fun addListener() {
        binding.btnExplore.setOnClickListener {
            // Navigate to HomeActivity
            val intent = Intent(requireContext(), com.gmwapp.dudeways.activity.HomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.btnRetry.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.putExtra("TARGET_FRAGMENT", "VerficationFragment")
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
