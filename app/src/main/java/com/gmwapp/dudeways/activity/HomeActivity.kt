package com.gmwapp.dudeways.activity

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
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
import com.google.android.material.navigation.NavigationBarView
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        mContext = this
        initUI(savedInstanceState)
        addListner()
        addObsereves()
    }

    private fun initUI(savedInstanceState: Bundle?) {

        session = Session(this)

        initializeComponents()
        initializeOneSignal()
        initializeZohoSalesIQ()
        setupBottomNavigation()
        loadProfilePicture()

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

    private fun addListner() {

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
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        settingsViewModel.settingLiveData.observe(this, Observer {
            if (it.success) {
                val array = it.data
                Log.e("UserDetails", "=" + Gson().toJson(it.data))

                session.setData(Constant.INSTAGRAM_LINK, array[0].instagram_link)
                session.setData(Constant.TELEGRAM_LINK, array[0].telegram_link)
                session.setData(Constant.UPI_ID, array[0].upi_id)

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
        Log.d(
            "USERDETAILS",
            "USERDETAILS VERIFIED_STATUS: " + session.getData(Constant.VERIFIED_STATUS)
        )

        val latitude = data.latitude
        val longitude = data.longtitude
        session.setData(Constant.LATITUDE, latitude)
        session.setData(Constant.LONGITUDE, longitude)

        if (latitude == "" && longitude == "") {
            getLocation()
        }


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


        binding.ivSearch.setOnClickListener {
            fm.beginTransaction().replace(R.id.fragment_container, SearchFragment()).commit()
        }

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
            "FkbMlSXKPaATeaZN35ZmjRdYaU29Wkx2QMkU75bCptU3ZA8TYZl2%2B%2BvFc55TAwVG_in",
            "xHGPBNAi6lC%2Fm7ngAEy%2FPSgch2eW42oPrw91hcyRHElKYqtGLQkB%2FuE%2F7QPdaD9BbNzYtKPn9U0kV316gEW6vUjMNMCKY5Jey7HFiemj%2BueB02iLgVZl6g%3D%3D",
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
            R.id.navNotification -> transaction.replace(
                R.id.fragment_container,
                NotificationFragment()
            )
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
        } else if (currentFragment is MyProfileFragment) {
            fm.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()

        } else {
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
                                    Toast.makeText(
                                        this,
                                        "Failed to get location. Please try again later.",
                                        Toast.LENGTH_SHORT
                                    ).show()
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