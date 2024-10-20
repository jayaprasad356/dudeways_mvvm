package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityIdverficationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IdverficationActivity : AppCompatActivity() {

    lateinit var binding: ActivityIdverficationBinding
    lateinit var mContext: IdverficationActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_idverfication)
        mContext = this
        initUI()
        addListner()
    }
    private fun initUI(){

    }
    private fun addListner(){
        binding.btnStart.setOnClickListener {
            val intent = Intent(mContext, Stage1Activity::class.java)
            startActivity(intent)
            finish()
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

    }
}