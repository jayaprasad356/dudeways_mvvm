package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmwapp.dudeways.New.PostActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.activity.MyTripsActivity
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentInterestBinding
import com.gmwapp.dudeways.databinding.FragmentTripBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripFragment : Fragment() {

    lateinit var binding: FragmentTripBinding
    lateinit var activity: Activity



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTripBinding.inflate(inflater, container, false)
        initUI()
        addListner()
        return binding.root
    }
    private fun initUI(){
        activity = requireActivity()

        (activity as HomeActivity).binding.rltoolbar.visibility = View.GONE
//        (activity as HomeActivity).binding.ivSearch.visibility = View.GONE

    }

    private fun addListner(){
        binding.btnStart.setOnClickListener {
            val intent = Intent(activity, PostActivity::class.java)
            startActivity(intent)
        }

        binding.btnMytrip.setOnClickListener {
            val intent = Intent(activity, MyTripsActivity::class.java)
            startActivity(intent)

        }

    }
}