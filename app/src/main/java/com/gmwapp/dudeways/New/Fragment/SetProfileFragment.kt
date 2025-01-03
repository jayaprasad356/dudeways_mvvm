package com.gmwapp.dudeways.New.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.HomeActivity
import com.gmwapp.dudeways.activity.ProfileActivity
import com.gmwapp.dudeways.activity.SplashActivity
import com.gmwapp.dudeways.databinding.FragmentSetProfileBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import com.google.ads.AdRequest.Gender
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class SetProfileFragment : Fragment() {
    lateinit var binding: FragmentSetProfileBinding
    private lateinit var activity: Activity
    lateinit var session: Session


    private var Language: String? = null


    var gender: String = ""
    var mobile: String = ""


    private val registerViewModel: RegisterViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_profile, container, false)
        session = Session(requireActivity())
       // setupSpinners()
        addListener()
        addObsereves()
        Language = arguments?.getString("language") ?: ""
        mobile = arguments?.getString("mobile_number") ?: ""

        //  Toast.makeText(requireContext(), "Selected Language: $Language", Toast.LENGTH_SHORT).show()


        return binding.root
    }

    private fun addObsereves() {
//
        registerViewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })


        registerViewModel.registerLiveData.observe(requireActivity(), Observer {
            if (it.success) {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                setPrefData(it.data)
            } else {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setPrefData(data: RegisterModel) {
        session.setData(Constant.USER_ID, data.id)
        session.setData(Constant.NAME, data.name)
        session.setData(Constant.UNIQUE_NAME, data.unique_name)
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(Constant.PROFESSION, data.profession)
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.MOBILE, data.mobile)
        session.setData(Constant.LANGUAGE, data.language)
        session.setData(Constant.REFER_CODE, data.refer_code)
        session.setData(Constant.INTRODUCTION, data.introduction)
        session.setBoolean("is_logged_in", true)
        val intent = Intent(requireActivity(), SplashActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }


    private fun setupSpinners() {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo currentYear - 49).map { it.toString() }

        // Show month selection dialog
        binding.etMonth.setOnClickListener {
            val monthDialog = AlertDialog.Builder(requireContext())
                .setTitle("Select Month")
                .setItems(months.toTypedArray()) { _, which ->
                    val selectedMonth = months[which]
                    binding.etMonth.setText(selectedMonth)
                }
                .show()
        }

        // Show year selection dialog
        binding.etYear.setOnClickListener {
            val yearDialog = AlertDialog.Builder(requireContext())
                .setTitle("Select Year")
                .setItems(years.toTypedArray()) { _, which ->
                    val selectedYear = years[which]
                    binding.etYear.setText(selectedYear)
                }
                .show()
        }

        // Show day selection dialog
        val days = (1..31).map { it.toString() }.toList()
        binding.etDay.setOnClickListener {
            val dayDialog = AlertDialog.Builder(requireContext())
                .setTitle("Select Day")
                .setItems(days.toTypedArray()) { _, which ->
                    val selectedDay = days[which]
                    binding.etDay.setText(selectedDay)
                }
                .show()
        }


    }


    private fun addListener() {
        // Listener for name input
        binding.etName.addTextChangedListener {
            validateFields()
        }

        // Listener for day selection
        binding.etDay.setOnClickListener {
            showDayPicker()
            validateFields()
        }

        // Listener for month selection
        binding.etMonth.setOnClickListener {
            showMonthPicker()
            validateFields()
        }

        // Listener for year selection
        binding.etYear.setOnClickListener {
            showYearPicker()
            validateFields()
        }

        // Listener for male gender selection
        binding.rlMale.setOnClickListener {
            gender = "male"
            binding.ivCheckmale.visibility = View.VISIBLE
            binding.ivCheckfemale.visibility = View.GONE
            validateFields()
        }

        // Listener for female gender selection
        binding.rlfemale.setOnClickListener {
            gender = "female"
            binding.ivCheckmale.visibility = View.GONE
            binding.ivCheckfemale.visibility = View.VISIBLE
            validateFields()
        }



        binding.btnDone.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val day = binding.etDay.text.toString().trim()
            val month = binding.etMonth.text.toString().trim()
            val year = binding.etYear.text.toString().trim()

            // Validation checks
            when {
                name.isEmpty() -> {
                    showSnackbar("Name cannot be empty")
                }

                day.isEmpty() || month.isEmpty() || year.isEmpty() -> {
                    showSnackbar("Please select a valid date of birth")
                }

                gender.isEmpty() -> {
                    showSnackbar("Please select a gender")
                }

                else -> {
                    // Close the keyboard
                    closeKeyboard()

                    val monthNumber = when (month) {
                        "January" -> "01"
                        "February" -> "02"
                        "March" -> "03"
                        "April" -> "04"
                        "May" -> "05"
                        "June" -> "06"
                        "July" -> "07"
                        "August" -> "08"
                        "September" -> "09"
                        "October" -> "10"
                        "November" -> "11"
                        "December" -> "12"
                        else -> "00" // Invalid month
                    }

                    val daynumber = when (day) {

                        "1" -> "01"
                        "2" -> "02"
                        "3" -> "03"
                        "4" -> "04"
                        "5" -> "05"
                        "6" -> "06"
                        "7" -> "07"
                        "8" -> "08"
                        "9" -> "09"
                        "10" -> "10"
                        "11" -> "11"
                        "12" -> "12"
                        "13" -> "13"
                        "14" -> "14"
                        "15" -> "15"
                        "16" -> "16"
                        "17" -> "17"
                        "18" -> "18"
                        "19" -> "19"
                        "20" -> "20"
                        "21" -> "21"
                        "22" -> "22"
                        "23" -> "23"
                        "24" -> "24"
                        "25" -> "25"
                        "26" -> "26"
                        "27" -> "27"
                        "28" -> "28"
                        "29" -> "29"
                        "30" ->"30"
                        "31" -> "31"
                        else -> {}
                    }


                    val dob = "$year-$monthNumber-$daynumber"





                    Log.d("SetProfileFragment", "Name: $name\n DOB: $dob\n  gender: $gender\n  Language: $Language")


                    if (isNetworkAvailable(requireActivity())) {
                        registerViewModel.doRegister(
                            mobile = mobile,
                            name = name,
                            dob = dob,
                            gender = gender,
                            language = Language.toString(),
                            email = "",
                            age = "",
                            proffessionId = "",
                            state = "",
                            city = "",
                            introduction = ""
                        )

                    } else {
                        Toast.makeText(
                            requireActivity(), getString(R.string.str_error_internet_connections),
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                    // Navigate to the next screen or fragment
//                        requireActivity().supportFragmentManager.beginTransaction()
//                            .setCustomAnimations(
//                                R.anim.slide_in_right, // Enter animation
//                                R.anim.slide_out_left, // Exit animation
//                                R.anim.slide_in_left,  // Pop Enter animation
//                                R.anim.slide_out_right // Pop Exit animation
//                            )
//                            .replace(
//                                R.id.fragment_container,
//                                VoiceTestFragment()
//                            ) // Replace with the actual fragment
//                            .addToBackStack(null)
//                            .commit()
                }
            }
        }
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusedView = requireActivity().currentFocus
        if (currentFocusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(
                currentFocusedView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun showDayPicker() {
        val days = (1..31).map { it.toString() }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Day")
            .setItems(days) { _, which ->
                binding.etDay.setText(days[which])
            }
            .show()
    }

    private fun showMonthPicker() {
        val months = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        AlertDialog.Builder(requireContext())
            .setTitle("Select Month")
            .setItems(months) { _, which ->
                binding.etMonth.setText(months[which])
            }
            .show()
    }

    private fun showYearPicker() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val years = (currentYear downTo currentYear - 49).map { it.toString() }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle("Select Year")
            .setItems(years) { _, which ->
                binding.etYear.setText(years[which])
            }
            .show()
    }


    private fun validateFields() {
        val name = binding.etName.text.toString().trim()
        val day = binding.etDay.text.toString().trim()
        val month = binding.etMonth.text.toString().trim()
        val year = binding.etYear.text.toString().trim()

        // Enable button only if all fields are filled
        binding.btnDone.isEnabled = name.isNotEmpty() && day.isNotEmpty() &&
                month.isNotEmpty() && year.isNotEmpty() && gender.isNotEmpty()



    }


}
