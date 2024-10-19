package com.gmwapp.dudeways.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.MyTripListAdapter
import com.gmwapp.dudeways.databinding.ActivityMyTripsBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.MyTripModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.TripViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyTripsActivity : AppCompatActivity(), MyTripListAdapter.onItemClick {

    lateinit var binding: ActivityMyTripsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val mytriplist = ArrayList<MyTripModel>()
    private lateinit var mytriplistAdapter: MyTripListAdapter
    private val viewModel: TripViewModel by viewModels()
    private var selectedPosition = -1

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_trips)
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        activity = this
        session = Session(activity)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvMytriplist.layoutManager = linearLayoutManager

        mytriplistAdapter = MyTripListAdapter(activity, mytriplist, this@MyTripsActivity)
        binding.rvMytriplist.adapter = mytriplistAdapter

    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.deleteTripLiveData.observe(this, Observer {
            if (it.success) {
                if (mytriplist.size > 0) {
                    mytriplist.removeAt(selectedPosition)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    mNetworkCallMyTripAPI()
                }
            } else {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.myTripLiveData.observe(this, Observer {
            if (it.success) {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                total = it.total.toInt()
                if (offset == 0) {
                    mytriplist.clear()
                }
                mytriplist.addAll(it.data)
                mytriplistAdapter?.notifyDataSetChanged()
                offset += limit


            } else {
                Toast.makeText(
                    activity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            mNetworkCallMyTripAPI()
        }

        binding.rvMytriplist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    mNetworkCallMyTripAPI()
                }
            }
        })

        if (isNetworkAvailable(activity)) {
            mNetworkCallMyTripAPI()
        } else {
            Toast.makeText(
                activity, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun mNetworkCallMyTripAPI() {
        if (isLoading) return
        isLoading = true

        viewModel.myTrip(
            session.getData(Constant.USER_ID).toString(),
            offset.toString(), limit.toString()
        )
    }

    override fun onDeleteClick(tripId: String, position: Int) {
        if (isNetworkAvailable(activity)) {
            selectedPosition = position
            viewModel.deleteTrip(tripId)
        } else {
            Toast.makeText(
                activity, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}