package com.gmwapp.dudeways.New.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentWelcomepageBinding

class WelcomepageFragment : Fragment() {

    private lateinit var binding: FragmentWelcomepageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using DataBindingUtil
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcomepage, container, false)
        addListener() // Initialize listeners
        return binding.root
    }

    private fun addListener() {
        binding.btnGetStarted.setOnClickListener {
            // Navigate to LoginpageFragment with slide animation
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_right, // Enter animation
                    R.anim.slide_out_left, // Exit animation
                    R.anim.slide_in_left,  // Pop Enter animation (when coming back)
                    R.anim.slide_out_right // Pop Exit animation (when going back)
                )
                .replace(R.id.fragment_container, LoginpageFragment())
                .addToBackStack(null) // Add to back stack
                .commit()
        }
    }

}
