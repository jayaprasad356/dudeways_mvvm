package com.gmwapp.dudeways.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityCustomerSupportBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.zoho.salesiqembed.ZohoSalesIQ
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerSupportActivity : BaseActivity() {

    lateinit var binding: ActivityCustomerSupportBinding
    lateinit var mContext: CustomerSupportActivity
    lateinit var session: Session


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_support)
        mContext = this
        initUI()
    }

    private fun initUI() {
        session = Session(mContext)
        binding.btnchatsupport.setOnClickListener {
            ZohoSalesIQ.Chat.show()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.rlInstagramLinkJoin.setOnClickListener {
            val link = session.getData(Constant.INSTAGRAM_LINK)
            val i = android.content.Intent(android.content.Intent.ACTION_VIEW)
            i.data = android.net.Uri.parse(link)
            startActivity(i)
        }

        binding.rlTelegramLinkJoin.setOnClickListener {
            val link = session.getData(Constant.TELEGRAM_LINK)
            val i = android.content.Intent(android.content.Intent.ACTION_VIEW)
            i.data = android.net.Uri.parse(link)
            startActivity(i)
        }


    }
}