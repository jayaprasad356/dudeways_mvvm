package com.gmwapp.dudeways.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityTripCompletedBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripCompletedActivity : AppCompatActivity() {

    lateinit var binding: ActivityTripCompletedBinding
    lateinit var mContext: TripCompletedActivity

    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trip_completed)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        activity = this
        session = Session(activity)

        session.setData(Constant.TRIP_LOCATION, "")
        session.setData(Constant.TRIP_TITLE, "")
        session.setData(Constant.TRIP_DESCRIPTION, "")
        session.setData(Constant.TRIP_FROM_DATE, "")
        session.setData(Constant.TRIP_TO_DATE, "")

    }

    private fun addListner() {
        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MyTripsActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}