package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityEarningBinding
import com.gmwapp.dudeways.fragment.EarningsBottomSheetFragment
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.EarningViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EarningActivity : BaseActivity() {

    private lateinit var binding: ActivityEarningBinding
    private lateinit var session: Session

    private val viewModel: EarningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning)
        session = Session(this)



        initUI()
        addListeners()
        addObservers()


    }

    private fun addObservers() {
        viewModel.withOutVerificationLiveData.observe(this) { response ->
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initUI() {
        val proof1 = session.getData(Constant.SELFIE_IMAGE)
        val proof2 = session.getData(Constant.FRONT_IMAGE)

        val verify = session.getData(Constant.VERIFIED)


        if (verify == "1") {
            binding.btnEnableWithVerification.apply {
                isEnabled = false
                setTextColor(resources.getColor(R.color.green))
                setBackgroundColor(resources.getColor(R.color.white))
                text = "Enabled"
                binding.cvWithoutVerification.visibility = View.GONE

            }
        }

        else if (verify == "2"){
            binding.btnEnableWithVerification.isEnabled = true
            binding.tvText.text = "* Rejeceted"
            binding.tvText.visibility = View.VISIBLE
        }


        else if (!proof1.isNullOrEmpty() && !proof2.isNullOrEmpty()) {
            binding.btnEnableWithVerification.apply {
                isEnabled = false
                setTextColor(resources.getColor(R.color.purple_700))
                setBackgroundColor(resources.getColor(R.color.primary_extra_light))
                text = "Under Verification"
                binding.tvText.text = "* with in 24 hours"
                binding.tvText.visibility = View.VISIBLE
            }
        } else {
            binding.tvText.visibility = View.GONE
            binding.btnEnableWithVerification.isEnabled = true
        }

        // Update button text and background color
        binding.btnEnableWithoutVerification.apply {
            setTextColor(resources.getColor(R.color.green))
            setBackgroundColor(resources.getColor(R.color.white))
            text = "Enabled"
            isEnabled = false
        }
    }

    private fun addListeners() {
        binding.btnEnableWithVerification.setOnClickListener {
            if (session.getData(Constant.MOBILE).isNullOrBlank()) {
                showEarningsBottomSheet()
            } else {
                navigateToProfileVerification()
            }
        }

        binding.btnEnableWithoutVerification.setOnClickListener {
            val uid = session.getData(Constant.USER_ID).orEmpty()
            val type = "without_verification"
            viewModel.doWithOutVerification(uid, type)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun showEarningsBottomSheet() {
        val bottomSheetFragment = EarningsBottomSheetFragment()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun navigateToProfileVerification() {
        val intent = Intent(this, ProfileVerifyActivity::class.java)
        startActivity(intent)
        finish()
    }
}
