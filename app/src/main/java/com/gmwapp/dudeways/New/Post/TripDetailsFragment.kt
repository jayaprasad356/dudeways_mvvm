package com.gmwapp.dudeways.New.Post

import android.app.Activity
import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentPlanningPostBinding
import com.gmwapp.dudeways.databinding.FragmentTripDetailsBinding
import java.util.Calendar


class TripDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTripDetailsBinding
    private lateinit var activity: Activity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_trip_details, container, false)
        activity = requireActivity()
        addListner()




        return binding.root
    }

    private fun addListner() {


        binding.etLocation.addTextChangedListener {
            validateFields()
        }


        binding.btnContinue.setOnClickListener {
            // Get input values from the EditText fields
            val location = binding.etLocation.text.toString().trim()
            val tripName = binding.ettext.text.toString().trim()

            // Validate input fields
            if (location.isEmpty()) {
                binding.etLocation.error = "Location cannot be empty"
                return@setOnClickListener
            }

            if (tripName.isEmpty()) {
                binding.ettext.error = "Trip name cannot be empty"
                return@setOnClickListener
            }


            val TripName = arguments?.getString("nameTrip") ?: ""


            val bundle = Bundle()
            bundle.putString("nameTrip", TripName)
            bundle.putString("location", location)
            bundle.putString("discription", tripName)
            // Navigate to DateofTripFragment with slide animation and pass the bundle

            val dateofTripFragment = DateofTripFragment()
            dateofTripFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right, // Enter animation
                    R.anim.slide_out_left, // Exit animation
                    R.anim.slide_in_left,  // Pop Enter animation (when coming back)
                    R.anim.slide_out_right // Pop Exit animation (when going back)
                )
                .replace(R.id.frameLayout, dateofTripFragment)
                .addToBackStack(null) // Add to back stack
                .commit()
        }
    }

    private fun validateFields() {
        // Validate input fields
        if (binding.etLocation.text.isNotEmpty()) {
            binding.btnContinue.isEnabled = true
            return
        }

       else if (binding.ettext.text.isNotEmpty()) {
            binding.btnContinue.isEnabled = true
            return
        }

        else {
            binding.btnContinue.isEnabled = false
            return
        }



    }


}