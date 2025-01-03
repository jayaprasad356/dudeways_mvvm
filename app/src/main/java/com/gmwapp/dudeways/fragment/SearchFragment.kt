package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.adapter.SearchAdapter
import com.gmwapp.dudeways.databinding.FragmentSearchBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.SearchModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    private lateinit var activity: Activity
    private lateinit var session: Session

    private lateinit var searchAdapter: SearchAdapter

    private val usersList = ArrayList<SearchModel>()
    private var isLoading = false
    private var total = 0
    private var offset = 0
    private val limit = 10
    private var selectedGender: String? = "all"  // Add gender variable


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        initUI()
        addListner()
        addObsereves()
        return binding.root
    }

    private fun initUI() {
        activity = requireActivity()
        session = Session(activity)

        if (usersList.isEmpty()) {
            loadProfileList()
        }
        setupSwipeRefreshLayout()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.GONE
//        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE

        setupRecyclerView()

        binding.ivMore.setOnClickListener {
            showPopupMenu()
        }


    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(activity, binding.ivMore)
        popupMenu.inflate(R.menu.filter_menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_male -> {
                    selectedGender = "Male"  // Set gender to Male
                    binding.ivMore.text = "Male"
                    binding.ivMore.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.male_ic,
                        0,
                        0,
                        0
                    )
                    binding.ivMore.compoundDrawables[0].setTint(resources.getColor(R.color.blue_200))
                    offset = 0  // Reset offset to 0
                    loadProfileList()  // Call API to load the male profiles
                    true
                }

                R.id.menu_female -> {
                    selectedGender = "Female"  // Set gender to Female
                    binding.ivMore.text = "Female"
                    binding.ivMore.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.female_ic,
                        0,
                        0,
                        0
                    )
                    binding.ivMore.compoundDrawables[0].setTint(resources.getColor(R.color.primary))
                    offset = 0  // Reset offset to 0
                    loadProfileList()  // Call API to load the female profiles
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    private fun addObsereves() {
        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.userLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                isLoading = false
                binding.swipeRefreshLayout.isRefreshing = false
                total = it.total.toInt()
                binding.rvSearch.visibility = View.VISIBLE
                if (offset == 0) {
                    usersList.clear()
                }

                usersList.addAll(it.data)

                if (offset == 0) {
                    searchAdapter?.notifyDataSetChanged()
                } else {
                    searchAdapter?.notifyItemRangeInserted(offset, usersList.size - 1)
                }

                offset += limit

            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.layoutManager = linearLayoutManager

        searchAdapter = SearchAdapter(activity, usersList)
        binding.rvSearch.adapter = searchAdapter

        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    loadProfileList()
                }
            }
        })
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loadProfileList()
        }

    }

    private fun loadProfileList() {
       /* if (isLoading) return
        isLoading = true
*/
        if (isNetworkAvailable(requireActivity())) {
            viewModel.getUsers(
                session.getData(Constant.USER_ID).toString(),
                offset.toString(),
                limit.toString(),
                selectedGender.toString()
            )
        } else {
            Toast.makeText(
                requireActivity(), getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onResume() {
        super.onResume()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                }
            })
    }

}