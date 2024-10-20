package com.gmwapp.dudeways.activity

import android.content.Intent
import android.net.Uri
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
import com.gmwapp.dudeways.databinding.ActivityRefundBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RefundActivity : BaseActivity() {

    lateinit var binding: ActivityRefundBinding
    lateinit var mContext: RefundActivity
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_refund)
        mContext = this
        initUI()
        addObsereves()
    }

    private fun initUI() {

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        // Set the background color of the WebView
        binding.wvPrivacy.setBackgroundColor(resources.getColor(R.color.primary_light, null))

        handleDeepLink(intent)

        if (isNetworkAvailable(mContext)) {
            viewModel.getRefundPolicy()
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun handleDeepLink(intent: Intent?) {
        val action = intent?.action
        val data: Uri? = intent?.data

        if (Intent.ACTION_VIEW == action && data != null) {
            // Extract the user ID and chat ID from the query parameters
            val userId = data.getQueryParameter("userid")
            val chatId = data.getQueryParameter("chatid")

            // Display the extracted user ID and chat ID in a toast message
            if (userId != null && chatId != null) {
                Toast.makeText(this, "User ID: $userId, Chat ID: $chatId", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "Missing user ID or chat ID", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.refundPolicyLiveData.observe(this, Observer {
            if (it.success) {
                binding.wvPrivacy.loadDataWithBaseURL(
                    "",
                    it.data[0].refund_policy,
                    "text/html",
                    "UTF-8",
                    ""
                )

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}