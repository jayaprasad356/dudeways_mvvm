package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.adapter.ConnectAdapter
import com.gmwapp.dudeways.databinding.FragmentInterestBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.ConnectModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.AddFriendViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterestFragment : Fragment() {

    lateinit var binding: FragmentInterestBinding
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var total = 0
    private val connectList = ArrayList<ConnectModel>()
    private val viewModel: AddFriendViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInterestBinding.inflate(inflater, container, false)
        initUI()
        addListner()
        addObsereves()
        return binding.root
    }

    private fun initUI() {

        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE
        (activity as HomeActivity).binding.ivSearch.visibility = View.VISIBLE


        setupRecyclerView()
        setupSwipeRefreshLayout()

        if (isNetworkAvailable(requireActivity())) {
            if (connectList.isEmpty()) {
                notificationList()
            }
        } else {
            Toast.makeText(
                requireActivity(), getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun addListner() {

    }

    private fun setupRecyclerView() {



        binding.rvConnectList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    notificationList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            notificationList()
        }
    }

    private fun notificationList() {
        if (isLoading) return
        isLoading = true

        viewModel.getConnect(
            session.getData(Constant.USER_ID).toString(),
            offset.toString(), limit.toString()
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

        viewModel.connectFriendLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                total = it.total.toInt()
                if (offset == 0) {
                    connectList.clear()
                }
                connectList.addAll(it.data)

                binding.apply {
                    rvConnectList.layoutManager = LinearLayoutManager(activity)
                    rvConnectList.setHasFixedSize(true)
                    adapter = ConnectAdapter(activity, connectList)
                    binding.rvConnectList.adapter = adapter
                }

                offset += limit
            }
        })
    }

}