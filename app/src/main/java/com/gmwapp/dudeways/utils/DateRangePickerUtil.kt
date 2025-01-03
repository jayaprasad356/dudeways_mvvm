package com.gmwapp.dudeways.utils

import android.app.DatePickerDialog
import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import java.util.*

object DateRangePickerUtil {

    // Function to show date range picker
    fun showDateRangePicker(
        context: Context,
        dateRangeFrame: FrameLayout,
        onDateRangeSelected: (startDate: Calendar, endDate: Calendar) -> Unit
    ) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        // Show start date picker
        showDatePickerDialog(context, startDate, isStartDate = true) { selectedStartDate ->
            // Once start date is selected, show end date picker
            showDatePickerDialog(context, endDate, isStartDate = false) { selectedEndDate ->
                // Once both dates are selected, pass the selected dates to the callback
                onDateRangeSelected(selectedStartDate, selectedEndDate)

                // Update UI with the selected date range
                updateDateRangeUI(dateRangeFrame, selectedStartDate, selectedEndDate)
            }
        }
    }

    // Function to show DatePickerDialog
    private fun showDatePickerDialog(
        context: Context,
        calendar: Calendar,
        isStartDate: Boolean,
        onDateSelected: (Calendar) -> Unit
    ) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Function to update the UI with the selected date range
    private fun updateDateRangeUI(dateRangeFrame: FrameLayout, startDate: Calendar, endDate: Calendar) {
        val textView = TextView(dateRangeFrame.context)
        val dateRangeText = "Start Date: ${startDate.time}\nEnd Date: ${endDate.time}"
        textView.text = dateRangeText
        dateRangeFrame.removeAllViews()
        dateRangeFrame.addView(textView)
    }
}
