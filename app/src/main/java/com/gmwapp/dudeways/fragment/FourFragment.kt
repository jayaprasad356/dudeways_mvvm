package com.gmwapp.dudeways.fragment

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.StarttripActivity
import com.gmwapp.dudeways.databinding.FragmentFourBinding
import com.gmwapp.dudeways.utils.Constant
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class FourFragment : Fragment() {

    lateinit var binding: FragmentFourBinding
    lateinit var activity: Activity
    private var fromDate: Calendar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_four, container, false)
        initUI()
        addListner()
        return binding.root
    }

    private fun initUI() {
        activity = requireActivity()

        (activity as StarttripActivity).binding.ivBack.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.visibility = View.VISIBLE
        (activity as StarttripActivity).binding.btnNext.text = "Next"


        if ((activity as StarttripActivity).session.getData(Constant.TRIP_FROM_DATE) != null) {
            binding.edStartDate.setText((activity as StarttripActivity).session.getData(Constant.TRIP_FROM_DATE))
        }

        if ((activity as StarttripActivity).session.getData(Constant.TRIP_TO_DATE) != null) {
            binding.edEndDate.setText((activity as StarttripActivity).session.getData(Constant.TRIP_TO_DATE))
        }

    }

    private fun addListner() {
        binding.edEndDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.edStartDate.setOnClickListener {
            showDatePickerDialog1()
        }

    }

    private fun showDatePickerDialog() {
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog window to full screen
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleTextView = dialog.findViewById<TextView>(R.id.titleTextView)
        val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)

        // Set the minimum date to today or the selected fromDate if it's not null
        calendarView.minDate = fromDate?.timeInMillis ?: System.currentTimeMillis()

        // Set listener to handle date selection
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Check if the selected date is after the from date
            if (fromDate != null && selectedDate.before(fromDate)) {
                Toast.makeText(activity, "To date must be after from date", Toast.LENGTH_SHORT).show()
            } else {
                // Format the selected date
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)


                // Set the formatted date to the EditText
                binding.edEndDate.setText(formattedDate)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDatePickerDialog1() {
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_calendar)
        dialog.setCanceledOnTouchOutside(true)

        // Set dialog window to full screen
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleTextView = dialog.findViewById<TextView>(R.id.titleTextView)
        val calendarView = dialog.findViewById<CalendarView>(R.id.calendarView)

        // Set the minimum date to today
        calendarView.minDate = System.currentTimeMillis()

        // Set listener to handle date selection
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            // Format the selected date
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            // Set the formatted date to the EditText
            binding.edStartDate.setText(formattedDate)

            // Store the selected from date
            fromDate = selectedDate

            dialog.dismiss()
        }

        dialog.show()
    }
}