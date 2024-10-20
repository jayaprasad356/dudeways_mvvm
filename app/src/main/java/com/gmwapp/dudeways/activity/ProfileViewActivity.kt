package com.gmwapp.dudeways.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityProfileViewBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class ProfileViewActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileViewBinding
    lateinit var activity: ProfileViewActivity

    lateinit var session: Session

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences


    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    var filePath1: String? = ""
    var imageUri: Uri? = null

    private val REQUEST_IMAGE_GALLERY = 2

    private val REQUEST_IMAGE_CAMERA = 3
    private var isCameraRequest = false

    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: ProfileViewModel by viewModels()
    private val registerViewModel: RegisterViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_view)
        activity = this
        initUI()
    }

    private fun initUI() {

        session = Session(activity)




        sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)!!

        binding.darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            activity?.recreate()


        }
        binding.rlDeletemyaccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        binding.rlRefund.setOnClickListener {
            val intent = Intent(activity, RefundActivity::class.java)
            startActivity(intent)
        }


        binding.rlWallet.setOnClickListener {
            val intent = Intent(activity, WalletActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In

        val verify = session.getData(Constant.VERIFIED)
//
//        if (verify == "1"){
//            binding.ivVerify.visibility = View.VISIBLE
//        }
//        else{
//            binding.ivVerify.visibility = View.GONE
//        }


        binding.ivAddProfile.setOnClickListener {
            isCameraRequest = false
            pickImageFromGallery()
        }

        /*  binding.ivEdit.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
*/
        binding.ivCamera.setOnClickListener {
            isCameraRequest = true
            pickImageFromGallery()
        }

        binding.rlNotifications.setOnClickListener {
            val intent = Intent(activity, NotificationActivity::class.java)
            startActivity(intent)
        }

        binding.rlPrivacy.setOnClickListener {
            val intent = Intent(activity, PrivacypolicyActivity::class.java)
            startActivity(intent)
        }


        binding.rlDarkmode.setOnClickListener {

        }


        binding.rlTermscondition.setOnClickListener {
            val intent = Intent(activity, TermsconditionActivity::class.java)
            startActivity(intent)
        }

        binding.rlInviteFriends.setOnClickListener {
            val intent = Intent(activity, InviteFriendsActivity::class.java)
            startActivity(intent)
        }

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val profile = session.getData(Constant.PROFILE)
        Glide.with(activity).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)
        Glide.with(activity).load(session.getData(Constant.COVER_IMG))
            .placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
//
//        binding.tvProfessional.text = session.getData(Constant.PROFESSION)
//        binding.tvCity.text = session.getData(Constant.CITY)
//        binding.tvState.text = session.getData(Constant.STATE)
//        binding.tvGender.text = session.getData(Constant.GENDER)
        binding.tvName.text = session.getData(Constant.NAME)
        binding.tvUsername.text = "@" + session.getData(Constant.UNIQUE_NAME)
//        //   binding.tvPlace.text = session.getData(Constant.CITY) + ", " + session.getData(Constant.STATE)
//        binding.tvIntroduction.text = session.getData(Constant.INTRODUCTION)


        val gender = session.getData(Constant.GENDER)
        Log.d("gender", "gender: $gender")
        val age = session.getData(Constant.AGE)
//        binding.ivAge.text = age
//
//        if(gender == "male") {
//            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.male_ic))
//        }
//        else if (gender == "female"){
//            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.female_ic))
//        }
//        else{
//            binding.ivGender.setBackgroundDrawable(resources.getDrawable(R.drawable.third_gender))
//        }
//
//        if (gender == "male") {
//            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_200))
//        } else if(gender == "female"){
//            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary))
//        }
//
//        else{
//            binding.ivGenderColor.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
//        }


        if (gender == "male") {
            binding.rlWallet.visibility = View.GONE
        } else {
            binding.rlWallet.visibility = View.VISIBLE
        }




        binding.rlMytrips.setOnClickListener {
            val intent = Intent(activity, MyTripsActivity::class.java)
            startActivity(intent)
        }


        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        binding.rlStorepoints.setOnClickListener {

            val dialogView =
                layoutInflater.inflate(R.layout.dilog_chat_point, null)

            val dialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()
            val title = dialogView.findViewById<TextView>(R.id.tvTitle)
            val btnPurchase =
                dialogView.findViewById<MaterialButton>(R.id.btnPurchase)
            val tvDescription =
                dialogView.findViewById<TextView>(R.id.tvDescription)
            val tvSubDescription =
                dialogView.findViewById<TextView>(R.id.tvSubDescription)

            tvDescription.text = "Spend 10 points for a one-hour conversation with a user"
            tvDescription.setTextColor(ContextCompat.getColor(activity, R.color.primary))

            tvSubDescription.text = "Minimum 10 points required to chat"

            title.text = "You have ${session.getData(Constant.POINTS)} Points"

            btnPurchase.setOnClickListener {
                val intent = Intent(activity, PurchasepointActivity::class.java)
                startActivity(intent)
                dialogBuilder.dismiss()
            }


            dialogBuilder.show()

//            val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
//
//            val dialogBuilder = AlertDialog.Builder(activity)
//                .setView(dialogView)
//                .create()
//            val title = dialogView.findViewById<TextView>(R.id.dialog_title)
//            val btnPurchase = dialogView.findViewById<LinearLayout>(R.id.btnPurchase)
//            val btnFreePoints = dialogView.findViewById<LinearLayout>(R.id.btnFreePoints)
//            val tv_min_points = dialogView.findViewById<TextView>(R.id.tv_min_points)
//
//            tv_min_points.visibility = View.GONE
//
//            title.text = "You have ${session.getData(Constant.POINTS)} Points"
//
//            btnPurchase.setOnClickListener {
//                val intent = Intent(activity, PurchasepointActivity::class.java)
//                startActivity(intent)
//                dialogBuilder.dismiss()
//            }
//
//            btnFreePoints.setOnClickListener {
//                val intent = Intent(activity, FreePointsActivity::class.java)
//                startActivity(intent)
//                dialogBuilder.dismiss()
//            }
//
//            dialogBuilder.show()
        }


        binding.rlDeactiveaccount.setOnClickListener {
            val intent = Intent(activity, DeactivateActivity::class.java)
            startActivity(intent)
        }

        binding.rlCustomerSupport.setOnClickListener {
            val intent = Intent(activity, CustomerSupportActivity::class.java)
            startActivity(intent)
        }

        binding.rlVerificationBadge.visibility = View.GONE

//        binding.rlVerificationBadge.setOnClickListener {
//
//
//            verification_list()
//
//
//
//        }

        binding.rlLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }


    }


    private fun showLogoutConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(activity)
            .setMessage("Are you sure you want to logout?")
            .setCancelable(true)
            .setPositiveButton("Logout") { dialog, _ ->
                // Perform logout action
                logout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialogBuilder.show()

        // Change button text colors
        dialogBuilder.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(activity, R.color.primary))
        dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(activity, R.color.text_grey))
    }


    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Delete My Account")
        builder.setMessage(
            "If you wish to permanently delete your Dudeways account, " +
                    "all your personal information, posts, messages, and data will be removed from our platform. " +
                    "This action is irreversible, and once your account is deleted, you will not be able to recover any information.\n\n" +
                    "Are you sure you want to proceed?"
        )

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Handle the deletion process here
            viewModel.deleteAccount(session.getData(Constant.USER_ID).toString())
            dialog.dismiss()
            // Optionally, you can trigger the account deletion process here
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog.show()


        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(
            ContextCompat.getColor(activity, R.color.primary)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(activity, R.color.text_grey)
        )
    }


    private fun addObsereves() {
        viewModel.isLoading.observe(activity, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.isLoading.observe(activity, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.registerLiveData.observe(activity, Observer {
            if (it.success) {
                session.setData(Constant.NAME, it.data.name)
                session.setData(Constant.UNIQUE_NAME, it.data.unique_name)
                session.setData(Constant.EMAIL, it.data.email)
                session.setData(Constant.AGE, it.data.age)
                session.setData(Constant.GENDER, it.data.gender)
                session.setData(Constant.PROFESSION, it.data.profession)
                session.setData(Constant.STATE, it.data.state)
                session.setData(Constant.CITY, it.data.city)
                session.setData(Constant.PROFILE, it.data.profile)
                session.setData(Constant.MOBILE, it.data.mobile)
                session.setData(Constant.REFER_CODE, it.data.refer_code)
                session.setData(Constant.REFERRED_BY, it.data.referred_by)
                Glide.with(activity).load(session.getData(Constant.PROFILE))
                    .placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

                // call resume() the HomeActivity
                onResume()


            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.coverImageLiveData.observe(activity, Observer {
            if (it.success) {
                session.setData(Constant.COVER_IMG, it.data.cover_img)
                Glide.with(activity).load(session.getData(Constant.COVER_IMG))
                    .placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
                onResume()
            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.deleteAccountLiveData.observe(activity, Observer {
            if (it.success) {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                logout()

            } else {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener(activity) {
            // Clear session data and redirect to login
            clearSessionData(activity)
            redirectToLogin(activity)
            session.clearData()


        }
    }

    private fun clearSessionData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private fun redirectToLogin(context: Context) {
        session.setBoolean("is_logged_in", false)
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        finish()
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                imageUri = data?.data
                if (isCameraRequest) {
                    // Configure CropImage for rectangular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(4, 2) // Set aspect ratio for a rectangle
                        .setCropShape(CropImageView.CropShape.RECTANGLE) // Set crop shape to rectangle
                        .start( this)
                } else {
                    // Configure CropImage for circular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                        .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                        .start( this)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(activity, true)
                    val imgFile = File(filePath1)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        if (isCameraRequest) {
                            binding.ivCover.setImageBitmap(myBitmap)
                            val uid: RequestBody = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                session.getData(Constant.USER_ID).toString()
                            )

                            var profileBody: MultipartBody.Part? = null
                            if (filePath1?.isNotEmpty() == true) {
                                val file = File(filePath1)
                                // create RequestBody instance from file
                                val requestFile = RequestBody.create(
                                    "multipart/form-data".toMediaTypeOrNull(), file
                                )
                                profileBody = MultipartBody.Part.createFormData(
                                    Constant.COVER_IMG, file.name, requestFile
                                )
                            }


                            viewModel.getCoverImage(
                                uid,
                                profileBody
                            )
                        } else {
                            binding.civProfile.setImageBitmap(myBitmap)
                            val uid: RequestBody = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                session.getData(Constant.USER_ID).toString()
                            )

                            var profileBody: MultipartBody.Part? = null
                            if (filePath1?.isNotEmpty() == true) {
                                val file = File(filePath1)
                                // create RequestBody instance from file
                                val requestFile = RequestBody.create(
                                    "multipart/form-data".toMediaTypeOrNull(), file
                                )
                                profileBody = MultipartBody.Part.createFormData(
                                    Constant.PROFILE, file.name, requestFile
                                )
                            }


                            registerViewModel.doUpdateImage(
                                uid,
                                profileBody
                            )
                        }
                    }
                }
            }
        }

    }
}