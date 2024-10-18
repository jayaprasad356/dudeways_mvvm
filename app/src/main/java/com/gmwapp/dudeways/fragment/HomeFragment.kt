package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.adapter.HomeProfilesAdapter
import com.gmwapp.dudeways.adapter.HomeUserlistAdapter
import com.gmwapp.dudeways.databinding.FragmentHomeBinding
import com.gmwapp.dudeways.databinding.LayoutHomeCategoryBinding
import com.gmwapp.dudeways.model.HomeCategory
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.model.HomeUserlist
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.HomeViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.collections.ArrayList


@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var homeCategorysAdapter: HomeCategorysAdapter
    private val homeCategoryList = ArrayList<HomeCategory>()
    private val homeProfileList = ArrayList<HomeProfile>()
    private val homeUserlist = ArrayList<HomeUserlist>()
    private lateinit var activity: Activity
    private lateinit var session: Session
    private var selectedItemPosition = 0
    private var offset = 0
    private val limit = 10
    private var isLoading = false
    private var currentType: String = "nearby"
    private var selectedDate: String? = ""
    private var total = 0
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        initUI()
        addObsereves()
        return binding.root

    }

    private fun initUI() {
        activity = requireActivity()
        session = Session(activity)

        (activity as HomeActivity).binding.rltoolbar.visibility = View.VISIBLE
        (activity as HomeActivity).binding.bottomNavigationView.visibility = View.VISIBLE
        (activity as HomeActivity).binding.ivSearch.visibility = View.VISIBLE

        loadCategoryList()

        binding.rvProfileList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1 && offset < total) {
                    loadProfileList(currentType)
                }
            }
        })

        if (homeProfileList.isEmpty()) {
            loadProfileList("female")
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            offset = 0
            loadProfileList(
                when (selectedItemPosition) {
                    0 -> "female"
                    1 -> "nearby"
                    2 -> "latest"
                    else -> "date"
                }
            )
        }
    }

    private fun loadProfileList(type: String) {
        viewModel.getTrip(
            session.getData(Constant.USER_ID).toString(),
            type, offset.toString(), limit.toString(), selectedDate.toString()
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

        viewModel.tripLiveData.observe(requireActivity(), Observer {
            isLoading = false
            binding.swipeRefreshLayout.isRefreshing = false
            if (it.success) {
                total = it.total.toInt()
                session.setData(Constant.TOTAL, total.toString())
                updateProfileList(it.data)
                loadActiveUserList()

            } else {
                showProfileListError(it.message.toString())
            }
        })

        viewModel.activeUserLiveData.observe(requireActivity(), Observer {
            isLoading = false
            if (it.success) {
                updateActiveUserList(it.data)
            } else {
                showActiveUserListError(it.message)
            }
        })
    }

    private fun updateActiveUserList(data: ArrayList<HomeUserlist>) {
        binding.rvUserList.visibility = View.VISIBLE

        homeUserlist.clear()

        homeUserlist.addAll(data)


        binding.apply {
            rvUserList.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            rvUserList.setHasFixedSize(true)
            homeUserListAdapter = HomeUserlistAdapter(activity, homeUserlist)
            rvUserList.adapter = homeUserListAdapter
            homeUserListAdapter?.notifyDataSetChanged()


        }

    }

    private fun showActiveUserListError(message: String) {
        binding.rvUserList.visibility = View.GONE
        // Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadActiveUserList() {
        if (isLoading) return
        isLoading = true

        viewModel.getActiveUser(session.getData(Constant.USER_ID).toString())
    }


    private fun updateProfileList(data: ArrayList<HomeProfile>) {
        binding.rvProfileList.visibility = View.VISIBLE

        if (offset == 0) {
            homeProfileList.clear()
        }
        homeProfileList.addAll(data)



        if (offset == 0) {
            binding.apply {
                rvProfileList.layoutManager = LinearLayoutManager(activity)
                rvProfileList.setHasFixedSize(true)
                homeProfileAdapter = HomeProfilesAdapter(activity, homeProfileList)
                rvProfileList.adapter = homeProfileAdapter

            }
        } else {
            binding.homeProfileAdapter?.notifyItemRangeInserted(offset, homeProfileList.size - 1)
        }

        offset += limit
    }


    private fun showProfileListError(message: String) {
        binding.rvProfileList.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private fun loadCategoryList() {
        homeCategoryList.clear()
        homeCategoryList.add(HomeCategory("1", "Female", ""))
        homeCategoryList.add(HomeCategory("1", "Nearby", ""))
        homeCategoryList.add(HomeCategory("1", "Latest", ""))
        homeCategoryList.add(HomeCategory("1", "Trip Date", ""))


        binding.apply {
            rvCategoryList.layoutManager = GridLayoutManager(activity, 4)
            rvCategoryList.setHasFixedSize(true)
            homeCategorysAdapter = HomeCategorysAdapter(requireActivity(), homeCategoryList)
            binding.rvCategoryList.adapter = homeCategorysAdapter
            homeCategorysAdapter?.notifyDataSetChanged()
        }
    }

    inner class HomeCategorysAdapter(
        private val activity: Activity,
        private val homeCategoryList: ArrayList<HomeCategory>
    ) : RecyclerView.Adapter<HomeCategorysAdapter.ItemHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                LayoutHomeCategoryBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val category = homeCategoryList[position]
            holder.binding.model = category

            holder.binding.tvName.text = if (position == 3 && selectedDate != null) {
                selectedDate
            } else {
                category.name
            }

            if (selectedItemPosition == position) {
                holder.binding.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.primary))
                holder.binding.tvName.setTextColor(activity.resources.getColor(R.color.white))
                holder.binding.tvName.setTypeface(null, Typeface.BOLD)
            } else {
                holder.binding.cardView.setCardBackgroundColor(activity.resources.getColor(R.color.white))
                holder.binding.tvName.setTextColor(activity.resources.getColor(R.color.black))
                holder.binding.tvName.setTypeface(null, Typeface.NORMAL)
            }

            holder.binding.cardView.setOnClickListener {
                val previousPosition = selectedItemPosition
                selectedItemPosition = position
                notifyItemChanged(previousPosition)
                notifyItemChanged(position)
                binding.rvProfileList.scrollToPosition(0)
                offset = 0
                when (position) {

                    0 -> {
                        currentType = "female"
                        loadProfileList(currentType)
                    }

                    1 -> {
                        currentType = "nearby"
                        loadProfileList(currentType)
                    }

                    2 -> {
                        currentType = "latest"
                        loadProfileList(currentType)
                    }

                    3 -> showDatePickerDialog(holder.binding.tvName)


                }
            }
        }

        override fun getItemCount(): Int {
            return homeCategoryList.size
        }

        inner class ItemHolder(val binding: LayoutHomeCategoryBinding) :
            RecyclerView.ViewHolder(binding.root) {

        }

        private fun showDatePickerDialog(tvName: TextView) {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
            dialog.setContentView(R.layout.dialog_calendar)
            dialog.setCanceledOnTouchOutside(true)

            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)
            calendarView.minDate = System.currentTimeMillis()

            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                this@HomeFragment.selectedDate = dateFormat.format(selectedDate.time)

                tvName.text = this@HomeFragment.selectedDate
                currentType = "date"
                loadProfileList(currentType)
                dialog.dismiss()
            }

            dialog.show()
        }
    }


}