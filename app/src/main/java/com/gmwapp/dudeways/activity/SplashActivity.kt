package com.gmwapp.dudeways.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivitySplashBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var mContext: SplashActivity
    lateinit var session: Session
    private val viewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        mContext = this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.logo_animation)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.start()
        binding.videoView.setOnCompletionListener {
            setData()
        }
    }

    private fun setData() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (session!!.getBoolean("is_logged_in")) {
                viewModel.doLogin(session.getData(Constant.EMAIL).toString())
            } else {
                val intent = Intent(mContext, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 100)
    }

    private fun addObsereves() {
        viewModel.loginLiveData.observe(this, Observer {
            if (it.success) {
                if (it.registered) {
                    session.setData(Constant.USER_ID, it.data.id)
                    session.setData(Constant.NAME, it.data.name)
                    session.setData(Constant.UNIQUE_NAME, it.data.unique_name)
                    session.setData(Constant.EMAIL, it.data.email)
                    session.setData(Constant.AGE, it.data.age)
                    session.setData(Constant.GENDER, it.data.gender)
                    session.setData(Constant.PROFESSION, it.data.profession)
                    session.setData(Constant.STATE, it.data.state)
                    session.setData(Constant.CITY, it.data.city)
                    session.setData(Constant.MOBILE, it.data.mobile)
                    session.setData(Constant.REFER_CODE, it.data.refer_code)
                    session.setData(Constant.COVER_IMG, it.data.cover_img)
                    session.setData(Constant.POINTS, it.data.points.toString())


                    if (GoogleSignIn.getLastSignedInAccount(this) != null) {
                        val intent = Intent(mContext, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                }
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

            }
        })
    }
}