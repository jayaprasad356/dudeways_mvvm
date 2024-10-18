package com.gmwapp.dudeways.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ProfessionAdapter
import com.gmwapp.dudeways.databinding.ActivityProfileDetailsBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.model.RegisterModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.LoginViewModel
import com.gmwapp.dudeways.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Field

@AndroidEntryPoint
class ProfileDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileDetailsBinding
    lateinit var mContext: ProfileDetailsActivity
    lateinit var session: Session

    var professions =
        mutableListOf<Pair<String, String>>() // Pair of (professionName, professionId)

    var select_option = "0"
    var gender = ""

    var profession_id = ""
    private val viewModel: LoginViewModel by viewModels()
    private val registerViewModel:RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_details)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        // Set custom cursor color
        binding.apply {
            setCursorDrawableColor(etName, R.drawable.color_cursor)
            setCursorDrawableColor(etEmail, R.drawable.color_cursor)
            setCursorDrawableColor(etMobileNumber, R.drawable.color_cursor)
            setCursorDrawableColor(etAge, R.drawable.color_cursor)
            setCursorDrawableColor(etProfession, R.drawable.color_cursor)
            setCursorDrawableColor(etcity, R.drawable.color_cursor)
            setCursorDrawableColor(etState, R.drawable.color_cursor)
            setCursorDrawableColor(etRefferCode, R.drawable.color_cursor)
        }

        if (isNetworkAvailable(mContext)) {
            viewModel.getProffession()
        } else {
            Toast.makeText(
                mContext, getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun addListner() {
        binding.apply {
            etIntroduction.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (binding.etIntroduction.lineCount > 3) {
                        val text =
                            s.toString().substring(0, binding.etIntroduction.selectionEnd - 1)
                        binding.etIntroduction.setText(text)
                        binding.etIntroduction.setSelection(binding.etIntroduction.text!!.length)
                    }
                }
            })

            llMale.setOnClickListener {
                llMale.backgroundTintList = resources.getColorStateList(R.color.primary)
                llFemale.backgroundTintList =
                    resources.getColorStateList(R.color.primary_extra_light)
                tvMale.setTextColor(resources.getColor(R.color.white))
                tvFemale.setTextColor(resources.getColor(R.color.black))
                select_option = "1"
                gender = "male"
            }

            llFemale.setOnClickListener {
                llFemale.backgroundTintList = resources.getColorStateList(R.color.primary)
                llMale.backgroundTintList =
                    resources.getColorStateList(R.color.primary_extra_light)
                tvMale.setTextColor(resources.getColor(R.color.black))
                tvFemale.setTextColor(resources.getColor(R.color.white))
                select_option = "2"
                gender = "female"
            }


            btnSave.setOnClickListener {
                if (isNetworkAvailable(mContext)) {
                    if (isValidate()) {
                        registerViewModel.doRegister(
                            name = binding.etName.text.toString().trim(),
                            email = session.getData(Constant.EMAIL).toString(),
                            binding.etAge.text.toString().trim(),
                            gender,
                            profession_id,
                            binding.etState.text.toString().trim(),
                            binding.etcity.text.toString().trim(),
                            binding.etIntroduction.text.toString().trim()
                        )
                    }
                } else {
                    Toast.makeText(
                        mContext, getString(R.string.str_error_internet_connections),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            etProfession.setOnClickListener {
                cardProfession.visibility = View.VISIBLE
                showProfessionDialog(etProfession)
            }
            etState.setOnClickListener {
                cardstate.visibility = View.VISIBLE
                showProfessionDialogstate(etState)
            }

            btnNext.setOnClickListener {
                nsProfileDetails.visibility = View.VISIBLE
                llDescribtion.visibility = View.GONE
            }

        }

    }

    @SuppressLint("SoonBlockedPrivateApi")
    private fun setCursorDrawableColor(editText: EditText, drawableRes: Int) {
        try {
            val drawable: Drawable = resources.getDrawable(drawableRes, null)
            val field: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            field.isAccessible = true
            field.set(editText, drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isValidate(): Boolean {

        var isValid = true

        if (binding.etName.text.toString().isEmpty()) {
            binding.etName.error = "Please enter name"
            isValid = false
        } else if (binding.etName.text.toString().length < 4) {
            binding.etName.error = "Name should be at least 4 characters"
            isValid = false
        } else if (select_option == "0") {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show()
        } else if (binding.etProfession.text.toString().isEmpty()) {
            binding.etProfession.error = "Please enter profession"
            isValid = false
        } else if (binding.etState.text.toString().isEmpty()) {
            binding.etState.error = "Please enter state"
            isValid = false
        } else if (binding.etcity.text.toString().isEmpty()) {
            binding.etcity.error = "Please enter city"
            isValid = false
        } else if (binding.etIntroduction.text.toString().isEmpty()) {
            binding.etIntroduction.error = "Please enter introduction"
            isValid = false
        } else if (binding.etIntroduction.text.toString().length < 15) {
            binding.etIntroduction.error = "Introduction should be at least 15 characters"
            isValid = false
        }

        return isValid
    }

    private fun showProfessionDialogstate(etState: EditText) {
        val State = listOf(
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
            "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand", "Karnataka", "Kerala",
            "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
            "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal"
        )

        binding.apply {
            val adapter = ProfessionAdapter(State) { selectedState ->
                etState.setText(selectedState)
                cardstate.visibility = View.GONE
                etState.error = null
            }
            rvState.adapter = adapter
            rvState.layoutManager = LinearLayoutManager(this@ProfileDetailsActivity)
        }

    }

    private fun showProfessionDialog(etProfession: EditText) {
        val adapter = ProfessionAdapter(professions.map { it.first }) { selectedProfession ->
            binding.etProfession.setText(selectedProfession)
            profession_id = professions.first { it.first == selectedProfession }.second
            binding.cardProfession.visibility = View.GONE
            binding.etProfession.error = null
        }
        binding.rvProfession.adapter = adapter
        binding.rvProfession.layoutManager = LinearLayoutManager(this)
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        registerViewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.proffessionLiveData.observe(this, Observer {
            if (it.success) {
                professions.clear()
                for (i in 0 until it.data.size) {
                    val professionObject = it.data[i]
                    val profession = professionObject.profession
                    val id = professionObject.id
                    professions.add(profession to id) // Store as Pair(professionName, professionId)
                }
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })

        registerViewModel.registerLiveData.observe(this, Observer {
            if (it.success){
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
                setPrefData(it.data)
            }else{
                Toast.makeText(mContext,it.message,Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setPrefData(data: RegisterModel) {
        session.setData(Constant.USER_ID, data.id)
        session.setData(Constant.NAME, data.name)
        session.setData(
            Constant.UNIQUE_NAME,
            data.unique_name
        )
        session.setData(Constant.EMAIL, data.email)
        session.setData(Constant.AGE, data.age)
        session.setData(Constant.GENDER, data.gender)
        session.setData(
            Constant.PROFESSION,
            data.profession
        )
        session.setData(Constant.STATE, data.state)
        session.setData(Constant.CITY, data.city)
        session.setData(Constant.MOBILE, data.mobile)

        session.setData(
            Constant.REFER_CODE,
            data.refer_code
        )
        session.setData(
            Constant.INTRODUCTION,
            data.introduction
        )
        session.setBoolean("is_logged_in", true)
        val intent = Intent(mContext, ProfileActivity::class.java)
        startActivity(intent)
        finish()

    }

}