package com.gmwapp.dudeways.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityVerifiedBinding
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifiedActivity : BaseActivity() {

    lateinit var binding: ActivityVerifiedBinding
    lateinit var mContext: VerifiedActivity
    lateinit var session: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verified)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        session = Session(mContext)

    }

    private fun addListner() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.ivBack.setOnClickListener{
            onBackPressed()
        }


    }
}