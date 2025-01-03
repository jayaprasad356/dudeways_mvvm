package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.New.Fragment.SelectLanguageFragment.LanguageAdapter
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.adapter.TripAdapter
import com.gmwapp.dudeways.databinding.FragmentOneBinding
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OneFragment : Fragment() {

    lateinit var binding: FragmentOneBinding
    lateinit var activity: Activity
    lateinit var session: Session

    data class Trip(
        val nameTrip: String,
        var isSelected: Boolean = false
    )

    private val languages = listOf(
        Trip("Mountains")
    )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_one, container, false)
        initUI()
        addListner()
        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
    }

    private fun initUI() {
        activity = requireActivity()
        session = Session(activity)


        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"

        tripplan()
    }




    private fun addListner() {

    }

    private fun tripplan() {
        val list = ArrayList<String>()
        list.add(R.drawable.road_trip_img.toString())
        list.add(R.drawable.adventur_trip_img.toString())
        list.add(R.drawable.trip_3.toString())
        list.add(R.drawable.airport_trip_img.toString())

        binding.apply {
            rvtrip.layoutManager = LinearLayoutManager(activity)
            rvtrip.setHasFixedSize(true)
            adapter = TripAdapter(activity, list)
            binding.rvtrip.adapter = adapter
        }

    }


    fun isItemSelected(): Boolean {
        return binding.adapter?.selectedItemPosition != RecyclerView.NO_POSITION
    }

}