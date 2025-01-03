package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.adapter.NotificationAdapter
import com.gmwapp.dudeways.databinding.FragmentNotificationBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.NotificationModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : Fragment() {

    lateinit var binding: FragmentNotificationBinding
    private lateinit var activity: Activity

    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val notificationList = ArrayList<NotificationModel>()
    private lateinit var notificationAdapter: NotificationAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        initUI()
        addListner()
        addOnsereves()
        return binding.root
    }


    private fun initUI() {
        activity = requireActivity()
        session = Session(activity)
//
//        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
//        (activity as HomeActivity).binding.ivNotification.visibility = View.VISIBLE

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE


        setupRecyclerView()
        setupSwipeRefreshLayout()

        NotificationList()

    }

    private fun NotificationList() {
        if (isLoading) return
        isLoading = true

        if (isNetworkAvailable(requireActivity())) {
            viewModel.getNotifications(
                session.getData(Constant.USER_ID).toString(),
                offset.toString(), limit.toString()
            )
        } else {
            Toast.makeText(
                requireActivity(), getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun addListner() {

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
//            startActivity(Intent(requireActivity(), HomeActivity::class.java))
//            requireActivity().finish()
        }



    }

    private fun addOnsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.notificationLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                total = it.total.toInt()
                if (offset == 0) {
                    notificationList.clear()
                }
                notificationList.addAll(it.data)
                notificationAdapter?.notifyDataSetChanged()
                offset += limit
                binding.swipeRefreshLayout.isRefreshing = false


            } else {
                Toast.makeText(
                    activity,
                    it.message,
                    Toast.LENGTH_SHORT
                ).show()
                binding.swipeRefreshLayout.isRefreshing = false

            }
        })
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvNotificationList.layoutManager = linearLayoutManager

        notificationAdapter = NotificationAdapter(activity, notificationList)
        binding.rvNotificationList.adapter = notificationAdapter

        binding.rvNotificationList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    NotificationList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            NotificationList()
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