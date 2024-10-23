package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityEarningBinding
import com.gmwapp.dudeways.databinding.ActivityIdverficationBinding

class EarningActivity : BaseActivity() {

    lateinit var binding: ActivityEarningBinding
    lateinit var mContext: EarningActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earning)
        mContext = this
        initUI()
        addListner()
    }
    private fun initUI(){

    }
    private fun addListner(){
        binding.btnEnableWithVerification.setOnClickListener {
            val intent = Intent(mContext, ProfileVerifyActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.btnEnableWithoutVerification.setOnClickListener {
            // button text color
            binding.btnEnableWithoutVerification.setTextColor(resources.getColor(R.color.green))
            // button background color
            binding.btnEnableWithoutVerification.setBackgroundColor(resources.getColor(R.color.white))

        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}