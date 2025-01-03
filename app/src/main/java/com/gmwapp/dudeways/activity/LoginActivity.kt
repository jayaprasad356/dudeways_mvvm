package com.gmwapp.dudeways.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityLoginBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.LoginModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mContext: LoginActivity
    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var session: Session
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        initializeZohoSalesIQ()

        session = Session(mContext)

        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = FirebaseAuth.getInstance()

        val login = session.getData(Constant.LOGIN)

        if (login == "1") {
            binding.tvmore.visibility = View.VISIBLE
        } else {
            binding.tvmore.visibility = View.GONE
        }


    }

    private fun addListner() {
        binding.apply {
            googlelogin.setOnClickListener {
                signInGoogle()
            }
            tvmore.setOnClickListener {
                showLoginDialog()

            }
            tvHelp.setOnClickListener {
                ZohoSalesIQ.Chat.show()

            }
        }
    }

    private fun initializeZohoSalesIQ() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(
            application,
            "FkbMlSXKPaDPJTBvIOKZ7yZS78x2e%2FYPV9rdVzhraTF1V22I2tahvt3bhEaayH7fqZso778jBuA%3D_in",
            "xHGPBNAi6lC%2Fm7ngAEy%2FPSgch2eW42oPMx3W53lFWhg0T6llUBSBjSop2i8OG6yCoFvD8S5JoocJ8T0px73PQ9wcBnopYxHRcVhmI2KKbM4%2ByWaWjs1gQA%3D%3D",
            initConfig,
            object :
                InitListener {
                override fun onInitSuccess() {
                    // Initialization successful
                }

                override fun onInitError(errorCode: Int, errorMessage: String) {
                    // Handle initialization errors
                }
            })
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }


    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                session.setData(Constant.EMAIL, account.email.toString())
                session.setData("login", "true")

                mNetworkCallLoginAPI()

            }
        }
    }

    private fun mNetworkCallLoginAPI() {
        if (isNetworkAvailable(mContext)) {
            viewModel.doLogin(session.getData(Constant.EMAIL).toString())
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
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

        viewModel.loginLiveData.observe(this, Observer {
            if (it.success) {
                if (it.registered) {
                    setPrefData(it.data)
                } else {
                    startActivity(Intent(mContext, ProfileDetailsActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(
                    mContext,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()

            }
        })
    }

    private fun setPrefData(data: LoginModel) {
        session.setData(Constant.USER_ID, data.id)
        session.setData(Constant.NAME, data.name)
        session.setData(Constant.UNIQUE_NAME, data.unique_name)
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(Constant.PROFESSION, data.profession)
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.MOBILE, data.mobile)
        session.setData(Constant.COVER_IMG, data.cover_img)
        session.setData(Constant.POINTS, data.points.toString())
        session.setData(Constant.REFER_CODE, data.refer_code)
        session.setData(Constant.INTRODUCTION, data.introduction)
        session.setBoolean("is_logged_in", true)
        val intent = Intent(mContext, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }


    private fun showLoginDialog() {
        val dialog = Dialog(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_login, null)

        dialog.setContentView(dialogView)

        val emailEditText: EditText = dialogView.findViewById(R.id.etEmail)
        val passwordEditText: EditText = dialogView.findViewById(R.id.etPassword)
        val loginButton: Button = dialogView.findViewById(R.id.btnLogin)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if the entered email and password match the predefined values
            if (email == "testdudeways@gmail.com" && password == "test@123") {
                // Move to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                session.setData(Constant.USER_ID, "3806")
                session.setBoolean("is_logged_in", true)
                startActivity(intent)
                dialog.dismiss() // Dismiss dialog after login
            } else {
                // Show an error message (e.g., Toast or Snackbar)
                emailEditText.error = "Invalid email or password"
                passwordEditText.error = "Invalid email or password"
            }
        }

        dialog.show()
    }


}