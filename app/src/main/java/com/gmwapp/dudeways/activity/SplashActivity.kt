package com.gmwapp.dudeways.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
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
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var mContext: SplashActivity
    lateinit var session: Session
    private var currentVersion: String? = ""

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


        handleIncomingIntent(intent)

    }


    private fun addObsereves() {

        viewModel.appUpdateLiveData.observe(this, Observer {
            if (it.success) {
                val latestVersion = it.data[0].app_version
                val link = it.data[0].link
                session.setData(Constant.LOGIN, it.data[0].login)

                //   Toast.makeText(activity,latestVersion + currentVersion!!.toInt() , Toast.LENGTH_SHORT).show()
                val description = it.data[0].description
                if (currentVersion!!.toInt() >= latestVersion.toInt()) {
//                    binding.videoView.setOnCompletionListener {
                        GotoActivity()
                        // Do something when the video ends
//                    }
                } else {
                    showUpdateDialog(link, description)
                }

            }
        })

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

    override fun onResume() {
        setupViews()
        super.onResume()
    }

    private fun setupViews() {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            currentVersion = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (isNetworkAvailable(mContext)) {

            viewModel.appUpdate()

        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val data: Uri? = intent.data
        data?.let {
            if (it.isHierarchical) {
                val referralCode = it.getQueryParameter("referralCode")
                referralCode?.let {
                    Log.d("Referral Code", "referralCode = $referralCode")
//                    Toast.makeText(this, "Referral Code: $referralCode", Toast.LENGTH_LONG).show()
                    // Handle the referral code (e.g., store it in shared preferences, use it to fetch referral-specific data, etc.)
                }
            }
        }
    }

    private fun showUpdateDialog(link: String, description: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_dialog_update, null)
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.setCancelable(false);

        val btnUpdate = view.findViewById<View>(R.id.btnUpdate)
        val dialogMessage = view.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = description
        btnUpdate.setOnClickListener(View.OnClickListener {
            val url = link;
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })


        // Customize your bottom dialog here
        // For example, you can set text, buttons, etc.

        bottomSheetDialog.show()
    }


    private fun GotoActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (session?.getBoolean("is_logged_in") == true) {
                viewModel.doLogin(session.getData(Constant.EMAIL).toString())
            } else {
                val intent = Intent(mContext, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }

        }, 100)
    }


    override fun onStart() {
        super.onStart()

    }


}