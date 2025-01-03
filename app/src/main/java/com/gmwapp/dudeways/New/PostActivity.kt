package com.gmwapp.dudeways.New

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.gmwapp.dudeways.New.Fragment.VoiceTestFragment
import com.gmwapp.dudeways.New.Post.DateofTripFragment
import com.gmwapp.dudeways.New.Post.PlanningPostFragment
import com.gmwapp.dudeways.New.Post.TripDetailsFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.BaseActivity
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.databinding.ActivityPostBinding
import com.gmwapp.dudeways.databinding.ActivityStarttripBinding
import com.gmwapp.dudeways.fragment.FiveFragment
import com.gmwapp.dudeways.fragment.FourFragment
import com.gmwapp.dudeways.fragment.OneFragment
import com.gmwapp.dudeways.fragment.SixFragment
import com.gmwapp.dudeways.fragment.ThreeFragment
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PostActivity : BaseActivity() {

    private lateinit var binding: ActivityPostBinding
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post)

        session = Session(this)
        initUI()
        addListeners()
    }

    private fun initUI() {
        // Load the default fragment
        replaceFragment(PlanningPostFragment(), addToBackStack = false)
    }

    private fun addListeners() {
        // Handle the back button click
        binding.ivBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)

        // Check if the current fragment is the initial fragment
        if (currentFragment is PlanningPostFragment) {
            // If yes, finish the activity
            finish()
        } else {
            // Otherwise, pop the fragment back stack
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                // Fallback to ensure activity exits
                finish()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }
}
