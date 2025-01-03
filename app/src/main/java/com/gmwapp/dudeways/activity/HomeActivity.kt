package com.gmwapp.dudeways.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gmwapp.dudeways.New.Fragment.ProfileFragment
import com.gmwapp.dudeways.New.Fragment.PurchasepointFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityHomeBinding
import com.gmwapp.dudeways.fragment.ExploreFragment
import com.gmwapp.dudeways.fragment.HomeFragment
import com.gmwapp.dudeways.fragment.InterestFragment
import com.gmwapp.dudeways.fragment.MessagesFragment
import com.gmwapp.dudeways.fragment.MyProfileFragment
import com.gmwapp.dudeways.fragment.NotificationFragment
import com.gmwapp.dudeways.fragment.SearchFragment
import com.gmwapp.dudeways.fragment.TripFragment
import com.gmwapp.dudeways.model.UserDataModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.HomeViewModel
import com.gmwapp.dudeways.viewmodel.SettingsViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.zegocloud.uikit.components.audiovideo.ZegoAvatarViewProvider
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.config.DurationUpdateListener
import com.zegocloud.uikit.prebuilt.call.config.ZegoCallDurationConfig
import com.zegocloud.uikit.prebuilt.call.core.invite.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.core.invite.advanced.ZegoCallInvitationInCallingConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.C
import java.io.IOException
import java.util.Locale


@AndroidEntryPoint
class HomeActivity : BaseActivity(), NavigationBarView.OnItemSelectedListener {

    lateinit var binding: ActivityHomeBinding
    lateinit var mContext: HomeActivity
    private lateinit var fm: FragmentManager

    val ONESIGNAL_APP_ID = "4f929ed9-584d-4208-a3e8-7de1ae4f679e"


    private var tripFragment = TripFragment()
    private var exploreFragment = ExploreFragment()
    private var messagesFragment = MessagesFragment()
    private var homeFragment = HomeFragment()
    private var searchFragment = SearchFragment()
    private var notification = NotificationFragment()
    private var myProfileFragment = MyProfileFragment()
    private var interestFragment = InterestFragment()

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    private var backPressedTime: Long = 0
    private lateinit var backToast: Toast


    // Job for managing ongoing API requests
    private var apiJob: Job? = null
    lateinit var session: Session
    private val viewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    var isAllFabsVisible: Boolean? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout> // Declare the BottomSheetBehavior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)


        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Adjust padding for the bottom navigation view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBarsInsets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        mContext = this
        initUI(savedInstanceState)
        addListner()
        addObsereves()

        // Check if there are any extras
        val fragmentToOpen = intent.getStringExtra("fragment_to_open")
        if (fragmentToOpen != null) {
            // Logic to open the desired fragment
            when (fragmentToOpen) {
                "desiredFragmentTag" -> {

                    fm.beginTransaction().replace(R.id.fragment_container, MessagesFragment()).commit()

                }
                // Add more cases if needed for other fragments
            }
        }

    }

    private fun initUI(savedInstanceState: Bundle?) {


//        binding.randomActionFab.visibility = View.GONE
//        binding.videoCallFab.visibility = View.GONE
//        binding.videoCallActionText.visibility = View.GONE

        session = Session(this)


        val gender = session.getData(Constant.GENDER)
        if (gender == "male") {
            binding.ivPoint.visibility = View.VISIBLE        }
        else if (gender == "female") {
            binding.ivPoint.visibility = View.GONE
        }

        binding.ivPointCoin.setText(session.getData(Constant.POINTS))



        initializeComponents()
        initializeOneSignal()
        initializeZohoSalesIQ()
        setupBottomNavigation()
        loadProfilePicture()
       // callfab()

        // Restore selected item from saved instance state if available
        if (savedInstanceState != null) {
            val selectedItemId = savedInstanceState.getInt("selectedItemId", R.id.navHome)
            binding.bottomNavigationView.selectedItemId = selectedItemId

            // Restore the current fragment based on the saved fragment tag
            val fragmentTag = savedInstanceState.getString("currentFragmentTag")
            if (fragmentTag != null) {
                val fragment = when (fragmentTag) {
                    HomeFragment::class.java.simpleName -> homeFragment
                    ExploreFragment::class.java.simpleName -> exploreFragment
                    MessagesFragment::class.java.simpleName -> messagesFragment
                    NotificationFragment::class.java.simpleName -> notification
                    MyProfileFragment::class.java.simpleName -> myProfileFragment
                    SearchFragment::class.java.simpleName -> searchFragment
                    InterestFragment::class.java.simpleName -> interestFragment
                    else -> homeFragment // default to homeFragment if unknown tag
                }
                fm.beginTransaction().replace(R.id.fragment_container, fragment).commit()
            }
        } else {
            // Set default fragment if no saved instance state
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
        }


    }

//    private fun callfab() {
//        val userId = session.getData(Constant.UNIQUE_NAME).toString()
//        val userName = session.getData(Constant.NAME).toString()
//
//        val videoCallFab = binding.videoCallFab
//        val audioCallFab = binding.audioCallFab
//        val videoCallActionText = binding.videoCallActionText
//        val audioCallActionText = binding.audioCallActionText
//        val mAddFab = binding.randomActionFab
//
//        setupZegoUIKit(userId,userName)
//
//
//
//        // Initially hide all sub FABs and action texts
//        audioCallFab.visibility = View.GONE
//        videoCallFab.visibility = View.GONE
//        audioCallActionText.visibility = View.GONE
//        videoCallActionText.visibility = View.GONE
//
//        // Set the visibility flag
//        isAllFabsVisible = false
//
//        // Set the initial icon for the extended state
//        mAddFab.setIconResource(R.drawable.random_ic) // Replace with your icon for shrinked state
//
//        // Set the initial state of the parent FAB
//        mAddFab.extend()
//
//        mAddFab.setOnClickListener {
//            if (!isAllFabsVisible!!) {
//                // Show sub FABs and action texts
//                audioCallFab.show()
//                videoCallFab.show()
//                audioCallActionText.visibility = View.VISIBLE
//                videoCallActionText.visibility = View.VISIBLE
//
//                // Change the icon and shrink the parent FAB
//                mAddFab.setIconResource(R.drawable.close_ic) // Replace with your icon for expanded state
//                mAddFab.shrink()
//
//                isAllFabsVisible = true
//            } else {
//                // Hide sub FABs and action texts
//                audioCallFab.hide()
//                videoCallFab.hide()
//                audioCallActionText.visibility = View.GONE
//                videoCallActionText.visibility = View.GONE
//
//                // Change the icon and extend the parent FAB
//                mAddFab.setIconResource(R.drawable.random_ic) // Replace with your icon for shrinked state
//                mAddFab.extend()
//
//                isAllFabsVisible = false
//            }
//        }
//
//        val CALL_MODE =  session.getData(Constant.CALL_MODE).toString()
//
//        if (CALL_MODE == "testing")
//        {
//            videoCallFab.setOnClickListener {
//                // Inflate the custom layout
//                val dialogView = layoutInflater.inflate(R.layout.dialog_call_custom, null)
//
//                // Create a dialog with the custom layout
//                val customDialog = AlertDialog.Builder(this)
//                    .setView(dialogView)
//                    .setCancelable(false)
//                    .create()
//
//                // Find views in the custom layout
//                val etCustomMessage = dialogView.findViewById<EditText>(R.id.etCustomMessage)
//                val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
//                val btnStartCall = dialogView.findViewById<Button>(R.id.btnStartCall)
//
//                // Set button actions
//                btnCancel.setOnClickListener {
//                    customDialog.dismiss()
//                }
//
//                btnStartCall.setOnClickListener {
//                    val customMessage = etCustomMessage.text.toString().trim()
//
//                    // Start the call with the custom message if provided
//                    if (customMessage.isNotEmpty()) {
//                        val intent = Intent(this, CallActivity::class.java).apply {
//                            putExtra("Userid", userId)
//                            putExtra("type", "video")
//                            putExtra("call_type", "test_call")
//                            putExtra("call_user_id", customMessage.toString())
//                        }
//                        startActivity(intent)
//
//                        // Setup Zego UIKit
//                        setupZegoUIKit(userId, userName)
//                    } else {
//                        Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
//                    }
//
//                    // Dismiss the dialog
//                    customDialog.dismiss()
//                }
//
//                // Show the dialog
//                customDialog.show()
//            }
//
//
//            audioCallFab.setOnClickListener {
//                // Inflate the custom layout for the dialog
//                val dialogView = layoutInflater.inflate(R.layout.dialog_call_custom, null)
//
//                // Create the dialog
//                val audioCallDialog = AlertDialog.Builder(this)
//                    .setView(dialogView)
//                    .setCancelable(false)
//                    .create()
//
//                // Find views in the custom dialog
//                val etAudioCallMessage = dialogView.findViewById<EditText>(R.id.etCustomMessage)
//                val btnAudioCallCancel = dialogView.findViewById<Button>(R.id.btnCancel)
//                val btnAudioCallStart = dialogView.findViewById<Button>(R.id.btnStartCall)
//
//                // Set up Cancel button
//                btnAudioCallCancel.setOnClickListener {
//                    audioCallDialog.dismiss() // Close the dialog
//                }
//
//                // Set up Start Call button
//                btnAudioCallStart.setOnClickListener {
//                    val customMessage = etAudioCallMessage.text.toString().trim()
//
//                    if (customMessage.isNotEmpty()) {
//                        // Start audio call with custom message
//                        val intent = Intent(this, CallActivity::class.java).apply {
//                            putExtra("Userid", userId)
//                            putExtra("type", "audio")
//                            putExtra("call_type", "test_call")
//                            putExtra("call_user_id", customMessage.toString())
//
//                        }
//                        startActivity(intent)
//
//                        // Setup Zego UIKit
//                        setupZegoUIKit(userId, userName)
//                        audioCallDialog.dismiss()
//                    } else {
//                        Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                // Show the dialog
//                audioCallDialog.show()
//            }
//        }
//
//        else if (CALL_MODE == "live")
//        {
//
//            // Set up click listeners for the sub FABs
//            videoCallFab.setOnClickListener {
//                val intent = Intent(this, CallActivity::class.java)
//                intent.putExtra("Userid", userId)
//                intent.putExtra("type", "video")
//                intent.putExtra("call_type", "random_call")
//                startActivity(intent)
//                setupZegoUIKit(userId,userName)
//            }
//
//            audioCallFab.setOnClickListener {
//                val intent = Intent(this, CallActivity::class.java)
//                intent.putExtra("Userid", userId)
//                intent.putExtra("type", "audio")
//                intent.putExtra("call_type", "random_call")
//                startActivity(intent)
//                setupZegoUIKit(userId,userName)
//            }
//
//        }
//
//
//
//
//
//
//    }

    private fun addListner() {

        binding.ivNotification.setOnClickListener {
            fm.beginTransaction().replace(R.id.fragment_container, NotificationFragment()).commit()
        }

        binding.ivPoint.setOnClickListener {
            fm.beginTransaction().replace(R.id.fragment_container, PurchasepointFragment()).commit()
        }


    }

    private fun setupZegoUIKit(Userid: Any, userName: String) {

        // Android's application context
        val application: Application = application

        val appID: Long = 1888627579
        val appSign: String = "00c8fd9ffedcbf5ecd66be30efcfb28928b1f83aa57f23106c7b38f369835af2"
        val userID: String = Userid.toString()
        val userName: String = userName.toString()


        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()


      //  callInvitationConfig.incomingCallRingtone = "outgoingcallringtone" // No file extension
      //  callInvitationConfig.outgoingCallRingtone = "outgoingcallringtone" // No file extension

        callInvitationConfig.callingConfig = ZegoCallInvitationInCallingConfig()

        // Whether to enable the feature of inviting users to an ongoing call
        callInvitationConfig.callingConfig.onlyInitiatorCanInvite = false


        // Set the custom call configuration provider
        callInvitationConfig.provider = object : ZegoUIKitPrebuiltCallConfigProvider {
            override fun requireConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig {
                val config: ZegoUIKitPrebuiltCallConfig = when {
                    invitationData.type == ZegoInvitationType.VIDEO_CALL.value && invitationData.invitees.size > 1 -> {
                        ZegoUIKitPrebuiltCallConfig.groupVideoCall()
                    }
                    invitationData.type != ZegoInvitationType.VIDEO_CALL.value && invitationData.invitees.size > 1 -> {
                        ZegoUIKitPrebuiltCallConfig.groupVoiceCall()
                    }
                    invitationData.type != ZegoInvitationType.VIDEO_CALL.value -> {
                        ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall()
                    }
                    else -> {
                        ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
                    }
                }

                // Set up call duration configuration with a listener
                config.durationConfig = ZegoCallDurationConfig().apply {
                    isVisible = true
                    durationUpdateListener = object : DurationUpdateListener {
                        override fun onDurationUpdate(seconds: Long) {
                            Log.d("TAG", "onDurationUpdate() called with: seconds = [$seconds]")
                            if (seconds.toInt() == 60 * 5) {  // Ends call after 5 minutes
                           //     ZegoUIKitPrebuiltCallService.endCall()
                            }
                        }
                    }
                }



//                config.avatarViewProvider = object : ZegoAvatarViewProvider {
//                    override fun onUserIDUpdated(parent: ViewGroup, uiKitUser: ZegoUIKitUser): View {
//                        val imageView = ImageView(parent.context)
//                        // Set different avatars for different users based on the user parameter in the callback.
//                        val avatarUrl = session.getData(Constant.PROFILE)
//                        if (!avatarUrl.isNullOrEmpty()) {
//                            val requestOptions = RequestOptions().circleCrop()
//                            Glide.with(parent.context).load(avatarUrl).apply(requestOptions).into(imageView)
//                        }
//                        return imageView
//                    }
//                }

                callInvitationConfig.provider = object : ZegoUIKitPrebuiltCallConfigProvider {
                    override fun requireConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig {
                        val config = ZegoUIKitPrebuiltCallInvitationConfig.generateDefaultConfig(invitationData)
                        // Modify the config settings here according to your business needs
                        return config
                    }
                }



                return config
            }
        }
// Initialize the Zego call service with the provided details



        ZegoUIKitPrebuiltCallService.init(application, appID, appSign, userID, userName, callInvitationConfig)

    }


    override fun onDestroy() {
        super.onDestroy()
        ZegoUIKitPrebuiltCallService.unInit()
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        settingsViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.locationLiveData.observe(this, Observer {
            if (it.success) {
             //   Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            } else {
              //  Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        settingsViewModel.settingLiveData.observe(this, Observer {
            if (it.success) {
                val array = it.data
                Log.e("UserDetails", "=" + Gson().toJson(it.data))

                session.setData(Constant.INSTAGRAM_LINK, array[0].instagram_link)
                session.setData(Constant.TELEGRAM_LINK, array[0].telegram_link)
                session.setData(Constant.UPI_ID, array[0].upi_id)
//                session.setData(Constant.MOBILE, array[0].mobile)
                session.setData(Constant.VERIFICATION_COST, array[0].verification_cost)
                session.setData(Constant.WITHOUT_VERIFICATION_COST, array[0].without_verification_cost)
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.userDetailLiveData.observe(this, Observer {
            if (it.success) {
                Log.e("UserDetails", "=" + Gson().toJson(it.data))
                setUserData(it.data)
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun setUserData(data: UserDataModel) {
        session.setData(Constant.NAME, data.name)
        session.setData(Constant.UNIQUE_NAME, data.unique_name)
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(Constant.PROFESSION, data.profession)
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.PROFILE, data.profile)
        session.setData(Constant.MOBILE, data.mobile)
        session.setData(Constant.REFER_CODE, data.refer_code)
        session.setData(Constant.COVER_IMG, data.cover_img)
        session.setData(Constant.POINTS, data.points.toString())
        session.setData(Constant.VERIFIED, data.verified.toString())
        session.setData(Constant.ONLINE_STATUS, data.online_status.toString())
        session.setData(Constant.INTRODUCTION, data.introduction)
        session.setData(Constant.MESSAGE_NOTIFY, data.message_notify.toString())
        session.setData(Constant.ADD_FRIEND_NOTIFY, data.add_friend_notify.toString())
        session.setData(Constant.VIEW_NOTIFY, data.view_notify.toString())
        session.setData(Constant.PROFILE_VERIFIED, data.profile_verified.toString())
//                        session.setData(Constant.CHAT_STATUS, jsonobj.getString(Constant.CHAT_STATUS))
        session.setData(Constant.UNREAD_COUNT, data.unread_count.toString())
        session.setData(Constant.VERIFIED_STATUS, data.verified.toString())
        session.setData(Constant.BALANCE, data.balance.toString())
        session.setData(Constant.SELFI_IMAGE, data.selfi_image.toString())
        session.setData(Constant.PROOF_IMAGE, data.proof_image.toString())
        Log.d(
            "USERDETAILS",
            "USERDETAILS VERIFIED_STATUS: " + session.getData(Constant.VERIFIED_STATUS)
        )

        val latitude = data.latitude
        val longitude = data.longtitude
        session.setData(Constant.LATITUDE, latitude)
        session.setData(Constant.LONGITUDE, longitude)


            getLocation()



        chatBadge()

    }

    private fun chatBadge() {
        // Toast.makeText(activity, session.getData(Constant.UNREAD_COUNT.toString()), Toast.LENGTH_SHORT).show()
        val chatBadge = binding.bottomNavigationView.getOrCreateBadge(R.id.navMessages)
        chatBadge.number = session.getData(Constant.UNREAD_COUNT.toString())?.toInt() ?: 0

        if (chatBadge.number == 0) {
            chatBadge.isVisible = false
        } else {
            chatBadge.isVisible = true
        }


        chatBadge.backgroundColor = ContextCompat.getColor(this, R.color.primary)
        chatBadge.badgeTextColor = ContextCompat.getColor(this, R.color.white)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the selected item id
        outState.putInt(
            "selectedItemId", binding.bottomNavigationView.selectedItemId ?: R.id.navHome
        )

        val currentFragment = fm.findFragmentById(R.id.fragment_container)
        if (currentFragment != null) {
            outState.putString("currentFragmentTag", currentFragment::class.java.simpleName)
        }

    }

    private fun initializeComponents() {
        fm = supportFragmentManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tripFragment = TripFragment()
        exploreFragment = ExploreFragment()
        messagesFragment = MessagesFragment()
        homeFragment = HomeFragment()
        notification = NotificationFragment()
        interestFragment = InterestFragment()
        myProfileFragment = MyProfileFragment()
        searchFragment = SearchFragment()

        fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()




        binding.civProfile.setOnClickListener {
            val transaction = fm.beginTransaction()
            transaction.replace(
                R.id.fragment_container, MyProfileFragment()
            )
            transaction.commit()

            //startActivity(Intent(mContext,ProfileViewActivity::class.java))
        }


    }

    private fun initializeOneSignal() {
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
        OneSignal.login("${session.getData(Constant.USER_ID)}")
    }

    private fun initializeZohoSalesIQ() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(application,
            "FkbMlSXKPaDPJTBvIOKZ7yZS78x2e%2FYPV9rdVzhraTF1V22I2tahvt3bhEaayH7fqZso778jBuA%3D_in",
            "xHGPBNAi6lC%2Fm7ngAEy%2FPSgch2eW42oPMx3W53lFWhg0T6llUBSBjSop2i8OG6yCoFvD8S5JoocJ8T0px73PQ9wcBnopYxHRcVhmI2KKbM4%2ByWaWjs1gQA%3D%3D",
            initConfig,
            object : InitListener {
                override fun onInitSuccess() {
                    // Initialization successful
                }

                override fun onInitError(errorCode: Int, errorMessage: String) {
                    // Handle initialization errors
                }
            })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(this)
        binding.bottomNavigationView.selectedItemId = R.id.navHome
    }

    private fun loadProfilePicture() {
        val profile = session.getData(Constant.PROFILE)
        Glide.with(mContext).load(profile).placeholder(R.drawable.profile_placeholder)
            .into(binding.civProfile)


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Cancel ongoing API requests
        apiJob?.cancel()

        val transaction = fm.beginTransaction()
        when (item.itemId) {
            R.id.navHome -> transaction.replace(R.id.fragment_container, HomeFragment())
            R.id.navExplore -> transaction.replace(R.id.fragment_container, TripFragment())
            R.id.navIntersts -> transaction.replace(R.id.fragment_container, InterestFragment())
            R.id.navMessages -> transaction.replace(R.id.fragment_container, MessagesFragment())
            R.id.navProfile -> transaction.replace(R.id.fragment_container, ProfileFragment())
        }
        transaction.commit()
        settingsViewModel.getSettings()
        return true

    }

    override fun onBackPressed() {
        val currentFragment = fm.findFragmentById(R.id.fragment_container)

        if (currentFragment is SearchFragment) {
            // If current fragment is SearchFragment, replace it with HomeFragment
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
        } else if (currentFragment is ProfileFragment) {
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()

        }else if (currentFragment is PurchasepointFragment){
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
        }
        else {
            // If not, follow the default behavior
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel()
                super.onBackPressed()
                return
            } else {
                backToast = Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT)
                backToast.show()
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        var addresses: List<Address>? = null
                        var retryCount = 3
                        while (retryCount > 0) {
                            try {
                                addresses = geocoder.getFromLocation(
                                    location.latitude, location.longitude, 1
                                )
                                if (addresses != null && addresses.isNotEmpty()) {
                                    break
                                }
                            } catch (e: IOException) {
                                Log.e(
                                    "GeocoderError",
                                    "Service not Available. Retries left: $retryCount",
                                    e
                                )
                                retryCount--
                                if (retryCount == 0) {
                                    Log.e(
                                        "GeocoderError", "Failed to get location after retries", e
                                    )
//                                    Toast.makeText(
//                                        this,
//                                        "Failed to get location. Please try again later.",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
                                }
                            }
                        }

                        if (addresses != null && addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latitude = address.latitude
                            val longitude = address.longitude
                            viewModel.updateLocation(
                                session.getData(Constant.USER_ID).toString(),
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
            ), permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    // onstart
    override fun onStart() {
        super.onStart()
        viewModel.getUserDetails(session.getData(Constant.USER_ID).toString(), "1")

    }

    // onstop
    override fun onStop() {
        super.onStop()
        apiJob?.cancel()
        viewModel.getUserDetails(session.getData(Constant.USER_ID).toString(), "0")
    }

    override fun onResume() {
        super.onResume()
        onStart()
    }


}