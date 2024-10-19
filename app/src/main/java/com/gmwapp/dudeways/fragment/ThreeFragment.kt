package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentThreeBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThreeFragment : Fragment() {

    lateinit var binding: FragmentThreeBinding
    lateinit var activity: Activity
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_three, container, false)
        initUI()
        addListner()
        return binding.root
    }

    private fun initUI() {

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


        if (session.getData(Constant.TRIP_LOCATION) != null) {
            binding.etLocation.setText(session.getData(Constant.TRIP_LOCATION))
        }


    }

    private fun addListner() {

    }

}