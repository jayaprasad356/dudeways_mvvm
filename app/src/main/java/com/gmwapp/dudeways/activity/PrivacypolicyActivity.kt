package com.gmwapp.dudeways.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityPrivacypolicyBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivacypolicyActivity : BaseActivity() {

    lateinit var binding: ActivityPrivacypolicyBinding
    lateinit var mContext: PrivacypolicyActivity
    private val viewModel:ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_privacypolicy)
        mContext = this
        initUI()
        addObsereves()
    }
    private fun initUI(){
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // Set the background color of the WebView
        binding.wvPrivacy.setBackgroundColor(resources.getColor(R.color.white, null))

        if (isNetworkAvailable(mContext)){
            viewModel.getPrivarcyPolicy()
        }else{
            Toast.makeText(mContext,getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun addObsereves(){
        viewModel.isLoading.observe(this, Observer {
            if(it){
                binding.pbLoadData.visibility=View.VISIBLE
            }else{
                binding.pbLoadData.visibility=View.GONE
            }
        })

        viewModel.privarcyPolicyLiveData.observe(this, Observer {
            if (it.success){
                binding.wvPrivacy.loadDataWithBaseURL("", it.data[0].privacy_policy, "text/html", "UTF-8", "")
            }else{
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }
        })
    }
}