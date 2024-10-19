package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentFiveBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FiveFragment : Fragment() {

    lateinit var binding: FragmentFiveBinding
    lateinit var activity: Activity
    lateinit var session: Session


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_five, container, false)
        initUI()
        addListner()
        return binding.root
    }
    private fun initUI(){

        activity = requireActivity()
        session = Session(activity)

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.etDescription.lineCount > 5) {
                    val text = s.toString().substring(0, binding.etDescription.selectionEnd - 1)
                    binding.etDescription.setText(text)
                    binding.etDescription.setSelection(binding.etDescription.text!!.length)
                }
            }
        })

        if (session.getData(Constant.TRIP_TITLE) != null) {
            binding.etTripName.setText(session.getData(Constant.TRIP_TITLE))
        }

        if (session.getData(Constant.TRIP_DESCRIPTION) != null) {
            binding.etDescription.setText(session.getData(Constant.TRIP_DESCRIPTION))
        }


    }
    private fun addListner(){

    }

}