package com.gmwapp.dudeways.fragment

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.CustomerSupportActivity
import com.gmwapp.dudeways.activity.DeactivateActivity
import com.gmwapp.dudeways.activity.EarningActivity
import com.gmwapp.dudeways.activity.FreePointsActivity
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.activity.InviteFriendsActivity
import com.gmwapp.dudeways.activity.LoginActivity
import com.gmwapp.dudeways.activity.MyTripsActivity
import com.gmwapp.dudeways.activity.NotificationActivity
import com.gmwapp.dudeways.activity.PrivacypolicyActivity
import com.gmwapp.dudeways.activity.PurchasepointActivity
import com.gmwapp.dudeways.activity.RefundActivity
import com.gmwapp.dudeways.activity.SpinActivity
import com.gmwapp.dudeways.activity.TermsconditionActivity
import com.gmwapp.dudeways.activity.WalletActivity
import com.gmwapp.dudeways.databinding.FragmentMyProfileBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ProfileViewModel
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.zoho.salesiqembed.ZohoSalesIQ
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@AndroidEntryPoint
class MyProfileFragment : Fragment() {

    lateinit var binding: FragmentMyProfileBinding
    lateinit var session: Session

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var sharedPreferences: SharedPreferences


    private var rewardedAd: RewardedAd? = null
    private val adId = "ca-app-pub-8693482193769963/5956761344"
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        initUI()
        addObsereves()
        return binding.root
    }

    private fun initUI() {

        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE
        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE

        loadRewardedVideoAd()

        sharedPreferences = activity?.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)!!


        binding.darkModeSwitch.setOnCheckedChangeListener(null)  // Clear previous listener

// Set the initial state from SharedPreferences
        binding.darkModeSwitch.isChecked = sharedPreferences.getBoolean("dark_mode", false)

// Reattach the listener
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Disable switch to prevent multiple rapid clicks
            binding.darkModeSwitch.isEnabled = false

            // Save the new state in SharedPreferences
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply()

            // Apply the theme change
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            // Recreate the activity to apply the new theme
            activity?.recreate()

            // Re-enable the switch after recreation
            binding.darkModeSwitch.isEnabled = true
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

        binding.rlEarning.setOnClickListener {
            val intent = Intent(activity, EarningActivity::class.java)
            startActivity(intent)

        }
        binding.ivAddProfile.setOnClickListener {
            isCameraRequest = false
            pickImageFromGallery()
        }

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

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val profile = session.getData(Constant.PROFILE)
        Glide.with(requireActivity()).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)
        Glide.with(requireActivity()).load(session.getData(Constant.COVER_IMG))
            .placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
        binding.tvName.text = session.getData(Constant.NAME)
        binding.tvUsername.text = "@" + session.getData(Constant.UNIQUE_NAME)
        val gender = session.getData(Constant.GENDER)
        Log.d("gender", "gender: $gender")
        val age = session.getData(Constant.AGE)


        if (gender == "male") {
            binding.rlWallet.visibility = View.GONE
            binding.rlEarning.visibility = View.GONE
            binding.rlStorepoints.visibility = View.VISIBLE
        } else {
            binding.rlWallet.visibility = View.VISIBLE
            binding.rlEarning.visibility = View.VISIBLE
            binding.rlStorepoints.visibility = View.GONE
        }
        binding.rlMytrips.setOnClickListener {
            val intent = Intent(activity, MyTripsActivity::class.java)
            startActivity(intent)
        }


        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
//            startActivity(Intent(requireActivity(), HomeActivity::class.java))
//            requireActivity().finish()
        }


        binding.rlStorepoints.setOnClickListener {
            val dialogView =
                requireActivity().layoutInflater.inflate(R.layout.dilog_chat_point, null)

            val dialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .create()
            val title = dialogView.findViewById<TextView>(R.id.tvTitle)
            val btnPurchase = dialogView.findViewById<MaterialButton>(R.id.btnPurchase)
            val btnFreePoints = dialogView.findViewById<MaterialButton>(R.id.btnFreePoints)
            val tvDescription =
                dialogView.findViewById<TextView>(R.id.tvDescription)
            val tvSubDescription =
                dialogView.findViewById<TextView>(R.id.tvSubDescription)

            tvDescription.text = "Spend 10 points for a one-hour conversation with a user"
            tvDescription.setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))

            tvSubDescription.text = "Minimum 10 points required to chat"

            title.text = "You have ${session.getData(Constant.POINTS)} Points"

            btnPurchase.setOnClickListener {
                val intent = Intent(activity, PurchasepointActivity::class.java)
                requireActivity().startActivity(intent)
                dialogBuilder.dismiss()
            }

            btnFreePoints.setOnClickListener {
//                loadRewardedVideoAd()
//                showRewardedVideoAd()

                val intent = Intent(activity, SpinActivity::class.java)
                requireActivity().startActivity(intent)
                dialogBuilder.dismiss()
            }

            dialogBuilder.show()
        }


        binding.rlDeactiveaccount.setOnClickListener {
            val intent = Intent(activity, DeactivateActivity::class.java)
            startActivity(intent)
        }
        binding.rlCustomerSupport.setOnClickListener {
            if (gender == "male") {
                // Open Gmail directly to compose an email
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("mailto:dudeways24@gmail.com") // Replace with your email
                    putExtra(Intent.EXTRA_SUBJECT, "Customer Support")
                    putExtra(Intent.EXTRA_TEXT, "Hello, I need help with...")
                }
                intent.setPackage("com.google.android.gm") // Ensures only Gmail app handles the intent

                try {
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "Gmail app not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Open Zoho SalesIQ chat
                ZohoSalesIQ.Chat.show()
            }
        }



        binding.rlVerificationBadge.visibility = View.GONE
        binding.rlLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }


    }

    private fun loadRewardedVideoAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(requireActivity(), adId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
                //  Toast.makeText(this@FreePointsActivity, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                //  Toast.makeText(this@FreePointsActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRewardedVideoAd() {
        rewardedAd?.let { ad ->
            ad.show(requireActivity()) { rewardItem: RewardItem ->

                val intent = Intent(activity, SpinActivity::class.java)
                requireActivity().startActivity(intent)
            }
        } ?: run {
            loadRewardedVideoAd()
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
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.primary))
        dialogBuilder.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.text_grey))
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
            ContextCompat.getColor(requireActivity(), R.color.primary)
        )
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
            ContextCompat.getColor(requireActivity(), R.color.text_grey)
        )
    }


    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.registerLiveData.observe(requireActivity(), Observer {
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
                Glide.with(requireActivity()).load(session.getData(Constant.PROFILE))
                    .placeholder(R.drawable.profile_placeholder).into(binding.civProfile)

                // call resume() the HomeActivity
                onResume()


            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.coverImageLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                session.setData(Constant.COVER_IMG, it.data.cover_img)
                Glide.with(requireActivity()).load(session.getData(Constant.COVER_IMG))
                    .placeholder(R.drawable.placeholder_bg).into(binding.ivCover)
                onResume()
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.deleteAccountLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                logout()

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Logout successful, clear session data and redirect
                Toast.makeText(requireActivity(), "Logout successful", Toast.LENGTH_SHORT).show()
                clearSessionData(requireActivity())
                session.clearData() // Ensure session data is cleared

                redirectToLogin(requireActivity())
            } else {
                // Handle failure scenario (if any)
                Toast.makeText(requireActivity(), "Logout failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
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
        requireActivity().finish()
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
                        .start(requireContext(), this)
                } else {
                    // Configure CropImage for circular crop
                    CropImage.activity(imageUri)
                        .setAspectRatio(1, 1) // Set aspect ratio to 1:1 for a square crop
                        .setCropShape(CropImageView.CropShape.OVAL) // Set crop shape to oval
                        .start(requireContext(), this)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result: CropImage.ActivityResult? = CropImage.getActivityResult(data)
                if (result != null) {
                    filePath1 = result.getUriFilePath(requireActivity(), true)
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


                            //  Toast.makeText(requireActivity(), "$profileBody", Toast.LENGTH_SHORT).show()

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


    override fun onResume() {
        super.onResume()
        // verification_list()
        handleOnBackPressed()

    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Replace the current fragment with HomeFragment
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                }
            })
    }
}