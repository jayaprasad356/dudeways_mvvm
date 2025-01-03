package com.gmwapp.dudeways.New

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.gmwapp.dudeways.New.Fragment.VoiceTestFragment
import com.gmwapp.dudeways.New.Fragment.VoiceVerficationFragment
import com.gmwapp.dudeways.New.Fragment.WelcomepageFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.BaseActivity
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.databinding.ActivityHomeBinding
import com.gmwapp.dudeways.databinding.ActivityLogin2Binding
import com.gmwapp.dudeways.fragment.MessagesFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    lateinit var binding: ActivityLogin2Binding
    private lateinit var mContext: LoginActivity
    private lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login2)
        mContext = this
        fm = supportFragmentManager
        initUI(savedInstanceState)
        addListner()
        addObsereves()
    }

    private fun initUI(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")
            when (targetFragment) {
                "WelcomePage" -> openWelcomePageFragment()
                "VerficationFragment" -> openVoiceverificationFragment()
                "VerficationTestFragment" -> openVoiceVerficationFragment()
                // Add other fragments if needed
            }
        }
    }

    private fun openVoiceVerficationFragment() {
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, VoiceVerficationFragment())
        transaction.commit()
    }

    private fun openVoiceverificationFragment() {
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, VoiceTestFragment())
        transaction.commit()
    }

    private fun openWelcomePageFragment() {
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.fragment_container, WelcomepageFragment())
        transaction.commit()
    }

    private fun addObsereves() {
    }

    private fun addListner() {
    }
}
