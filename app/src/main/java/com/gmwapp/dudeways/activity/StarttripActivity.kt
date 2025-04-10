package com.gmwapp.dudeways.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityStarttripBinding
import com.gmwapp.dudeways.fragment.FiveFragment
import com.gmwapp.dudeways.fragment.FourFragment
import com.gmwapp.dudeways.fragment.OneFragment
import com.gmwapp.dudeways.fragment.SixFragment
import com.gmwapp.dudeways.fragment.ThreeFragment
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StarttripActivity : BaseActivity() {

    lateinit var binding: ActivityStarttripBinding
    lateinit var mContext: StarttripActivity
    lateinit var fm: FragmentManager
    lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_starttrip)
        mContext = this
        initUI()
        addListner()
    }

    private fun initUI() {
        session = Session(mContext)


        fm = supportFragmentManager

        val fragment_one = OneFragment()


        fm.beginTransaction().replace(R.id.frameLayout, fragment_one).commit()

    }
    // resume

    public override fun onResume() {
        super.onResume()
        // check the current fragment
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is OneFragment -> {


            }
            is ThreeFragment -> {


            }
            is FourFragment -> {

            }
            is FiveFragment -> {


            }
            is SixFragment -> {

            }
        }

    }

    private fun btnBack(it: View?) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is OneFragment -> {
                onBackPressed()
                onResume()
            }

            is ThreeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, OneFragment()).commit()
                onResume()

            }
            is FourFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, ThreeFragment()).commit()
                onResume()

            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                onResume()

            }
            is SixFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                onResume()

            }
        }
    }



    private fun addListner() {
        binding.btnNext.setOnClickListener {
            btnNext(it)
        }

        binding.ivBack.setOnClickListener{
            btnBack(it)
        }

    }

    fun btnNext(view: View) {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is OneFragment -> {

                if (fragment.isItemSelected()) {
                    fm.beginTransaction().replace(R.id.frameLayout, ThreeFragment()).commit()
                    onResume()
                } else {
                    Toast.makeText(this, "Please select a trip plan", Toast.LENGTH_SHORT).show()
                }
            }

            is ThreeFragment -> {
                // call fragment etLocation
                if ((fragment as ThreeFragment).binding.etLocation.text.toString().isEmpty()) {
                    (fragment as ThreeFragment).binding.etLocation.error = "Please enter location"
                }
                else {
                    session.setData(Constant.TRIP_LOCATION, (fragment as ThreeFragment).binding.etLocation.text.toString())
                    fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                    onResume()
                }



            }
            is FourFragment -> {
                if ((fragment as FourFragment).binding.edStartDate.text.toString().isEmpty()) {
                    (fragment as FourFragment).binding.edStartDate.error = "Please enter start date"
                } else if ((fragment as FourFragment).binding.edEndDate.text.toString().isEmpty()) {
                    (fragment as FourFragment).binding.edEndDate.error = "Please enter end date"
                } else {
                    session.setData(Constant.TRIP_FROM_DATE, (fragment as FourFragment).binding.edStartDate.text.toString())
                    session.setData(Constant.TRIP_TO_DATE, (fragment as FourFragment).binding.edEndDate.text.toString())
                    fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                    onResume()
                }

            }
            is FiveFragment -> {
                if ((fragment as FiveFragment).binding.etTripName.text.toString().isEmpty()) {
                    (fragment as FiveFragment).binding.etTripName.error = "Please enter trip name"
                }else if ((fragment as FiveFragment).binding.etDescription.text.toString().isEmpty()) {
                    (fragment as FiveFragment).binding.etDescription.error = "Please enter description"
                }
                else {
                    session.setData(Constant.TRIP_TITLE, (fragment as FiveFragment).binding.etTripName.text.toString())
                    session.setData(Constant.TRIP_DESCRIPTION, (fragment as FiveFragment).binding.etDescription.text.toString())
                    fm.beginTransaction().replace(R.id.frameLayout, SixFragment()).commit()
                    onResume()
                }


            }
            is SixFragment -> {


                // fragment as SixFragment call function addtrip
                (fragment as SixFragment).addTrip()

            }

        } }





    // back press
    override fun onBackPressed() {
        val fragment = fm.findFragmentById(R.id.frameLayout)
        when (fragment) {
            is OneFragment -> {
                super.onBackPressed()
            }
            is ThreeFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, OneFragment()).commit()
                onResume()
            }
            is FourFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, ThreeFragment()).commit()
                onResume()
            }
            is FiveFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FourFragment()).commit()
                onResume()
            }
            is SixFragment -> {
                fm.beginTransaction().replace(R.id.frameLayout, FiveFragment()).commit()
                onResume()
            }
        }
    }



}