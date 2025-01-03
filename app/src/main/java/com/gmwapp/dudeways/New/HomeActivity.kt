package com.gmwapp.dudeways.New

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.gmwapp.dudeways.New.Fragment.HomeFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.BaseActivity
import com.gmwapp.dudeways.databinding.ActivityHome2Binding
import com.google.android.material.navigation.NavigationBarView


class HomeActivity : BaseActivity(),  NavigationBarView.OnItemSelectedListener {


    lateinit var binding: ActivityHome2Binding
    lateinit var mContext: com.gmwapp.dudeways.New.HomeActivity
    private lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home2)
        mContext = this
        initUI(savedInstanceState)
        addListner()
        addObsereves()

        // Check if there are any extras
        val fragmentToOpen = intent.getStringExtra("fragment_to_open")
        if (fragmentToOpen != null) {
            // Logic to open the desired fragment
            when (fragmentToOpen) {
                "desiredFragmentTag" -> {

                    fm.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()

                }
                // Add more cases if needed for other fragments
            }
        }

    }

    private fun initUI(savedInstanceState: Bundle?) {






    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val transaction = fm.beginTransaction()
        when (item.itemId) {
            R.id.navHome -> transaction.replace(R.id.fragment_container, com.gmwapp.dudeways.fragment.HomeFragment())


        }
        transaction.commit()
        return true
    }

    private fun addListner() {

    }

    private  fun  addObsereves()  {

    }

}