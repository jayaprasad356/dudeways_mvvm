package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileVerifyBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class ProfileVerifyActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileVerifyBinding
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_verify)
        session = Session(this)

        initUI()
        setupListeners()
    }

    private fun initUI() {
        val proof1 = session.getData(Constant.SELFIE_IMAGE)
        val proof2 = session.getData(Constant.FRONT_IMAGE)
        val proof3 = session.getData(Constant.BACK_IMAGE)

        // Handle proof1 display
        if (proof1.isNullOrEmpty()) {
            binding.cvUploadSelfie.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.primary_extra_light)
            )
        } else {
            ContextCompat.getColor(this, R.color.green)
        }

        // Handle proof2 and proof3 display
        if (proof2.isNullOrEmpty() || proof3.isNullOrEmpty()) {
            binding.cvUploadIDProof.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.primary_extra_light)
            )
        } else {
            binding.cvUploadIDProof.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.green)
            )
        }
    }


    private fun setupListeners() {
        val proof1 = session.getData(Constant.SELFIE_IMAGE)
        val proof2 = session.getData(Constant.FRONT_IMAGE)
        val proof3 = session.getData(Constant.BACK_IMAGE)
        binding.ivBack.setOnClickListener { onBackPressed() }

        binding.cvUploadSelfie.setOnClickListener {

            if (proof1?.isEmpty() == true) {
                val intent = Intent(this, Stage2Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            } else {

            }

        }

        binding.cvUploadIDProof.setOnClickListener {
            if (proof2?.isEmpty() == true || proof3?.isEmpty() == true) {
                val intent = Intent(this, Stage3Activity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            } else {

            }

        }

    }
}
