package com.gmwapp.dudeways.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityNotificationBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.HomeViewModel
import com.gmwapp.dudeways.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseActivity() {

    lateinit var binding: ActivityNotificationBinding
    lateinit var mContext: NotificationActivity
    lateinit var session: Session
    private val viewModel: NotificationViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        mContext = this
        initUI()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        val checkBox1 = binding.cb1
        val layout1 = findViewById<LinearLayout>(R.id.bdLayout1)

        val checkBox2 = binding.cb2
        val layout2 = findViewById<LinearLayout>(R.id.bdLayout2)

        val checkBox3 = binding.cb3
        val layout3 = findViewById<LinearLayout>(R.id.bdLayout3)

        val checkBox4 = binding.cb4
        val layout4 = findViewById<LinearLayout>(R.id.bdLayout4)

        val checkBox5 = binding.cb5
        val layout5 = findViewById<LinearLayout>(R.id.bdLayout5)

        if (session.getData(Constant.MESSAGE_NOTIFY).equals("1", ignoreCase = true)) {
            checkBox1.isChecked = true
            layout1.setBackgroundResource(R.drawable.notification_select_border)
        } else {
            layout1.setBackgroundResource(R.drawable.notification_unselect_border)
        }

        if (session.getData(Constant.ADD_FRIEND_NOTIFY).equals("1", ignoreCase = true)) {
            checkBox3.isChecked = true
            layout3.setBackgroundResource(R.drawable.notification_select_border)
        } else {
            layout3.setBackgroundResource(R.drawable.notification_unselect_border)
        }

        if (session.getData(Constant.VIEW_NOTIFY).equals("1", ignoreCase = true)) {
            checkBox4.isChecked = true
            layout4.setBackgroundResource(R.drawable.notification_select_border)
        } else {
            layout4.setBackgroundResource(R.drawable.notification_unselect_border)
        }

        // Setting the change listeners
        checkBox1.setOnCheckedChangeListener { _, isChecked ->
            layout1.setBackgroundResource(if (isChecked) R.drawable.notification_select_border else R.drawable.notification_unselect_border)
        }

        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            layout2.setBackgroundResource(if (isChecked) R.drawable.notification_select_border else R.drawable.notification_unselect_border)
        }

        checkBox3.setOnCheckedChangeListener { _, isChecked ->
            layout3.setBackgroundResource(if (isChecked) R.drawable.notification_select_border else R.drawable.notification_unselect_border)
        }

        checkBox4.setOnCheckedChangeListener { _, isChecked ->
            layout4.setBackgroundResource(if (isChecked) R.drawable.notification_select_border else R.drawable.notification_unselect_border)
        }

        checkBox5.setOnCheckedChangeListener { _, isChecked ->
            layout5.setBackgroundResource(if (isChecked) R.drawable.notification_select_border else R.drawable.notification_unselect_border)
        }

        binding.btnUpdate.setOnClickListener {
            val messageNotify = if (checkBox1.isChecked) "1" else "0"
            val addFriendNotify = if (checkBox3.isChecked) "1" else "0"
            val viewNotify = if (checkBox4.isChecked) "1" else "0"
            viewModel.updateNotifications(
                session.getData(Constant.USER_ID).toString(),
                messageNotify.toString(), addFriendNotify.toString(), viewNotify.toString()
            )
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

        homeViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.updateNotificationLiveData.observe(this, Observer {
            if (it.success) {
                homeViewModel.getUserDetails(session.getData(Constant.USER_ID).toString(), "")
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        homeViewModel.userDetailLiveData.observe(this, Observer {
            if (it.success) {
                session.setData(Constant.MESSAGE_NOTIFY, it.data.message_notify.toString())
                session.setData(Constant.ADD_FRIEND_NOTIFY, it.data.add_friend_notify.toString())
                session.setData(Constant.VIEW_NOTIFY, it.data.view_notify.toString())

                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })


    }

}