package com.gmwapp.dudeways.New.Post

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentDateofTripBinding
import com.gmwapp.dudeways.databinding.ItemCalendarDayBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class DateofTripFragment : Fragment() {

    private lateinit var binding: FragmentDateofTripBinding
    private lateinit var activity: Activity

    data class CalendarDay(
        val date: LocalDate,
        var isSelected: Boolean = false,
        var isRangeStart: Boolean = false,
        var isRangeEnd: Boolean = false
    )

    private lateinit var calendarAdapter: CalendarAdapter
    private var days = mutableListOf<CalendarDay>()
    private var selectedStartDate: LocalDate? = null
    private var selectedEndDate: LocalDate? = null
    private var currentYearMonth: YearMonth = YearMonth.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dateof_trip, container, false)
        activity = requireActivity()
        addListner()

        setupUI()
        loadDaysForMonth(currentYearMonth)

        return binding.root
    }

    private fun addListner() {
        binding.btnContinue.setOnClickListener {
            // Get the input dates from the EditTexts
            val fromDate = binding.etFromDate.text.toString().trim()
            val toDate = binding.etToDate.text.toString().trim()

            // Validate if both FromDate and ToDate are not empty
            if (fromDate.isEmpty()) {
                // Show a Toast or display an error message for FromDate
                Toast.makeText(requireContext(), "Please select a From Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

           else if (toDate.isEmpty()) {
                // Show a Toast or display an error message for ToDate
                Toast.makeText(requireContext(), "Please select a To Date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            else{



                val TripName = arguments?.getString("nameTrip") ?: ""
                val location = arguments?.getString("location") ?: ""
                val tripName = arguments?.getString("discription") ?: ""


                val bundle = Bundle()
                bundle.putString("nameTrip", TripName)
                bundle.putString("location", location)
                bundle.putString("discription", tripName)
                bundle.putString("fromDate", fromDate)
                bundle.putString("toDate", toDate)


                // Navigate to TripDetailsFragment with slide animation and pass the bundle
                val tripImageFragment = TripimageFragment()
                tripImageFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right, // Enter animation
                        R.anim.slide_out_left, // Exit animation
                        R.anim.slide_in_left,  // Pop Enter animation (when coming back)
                        R.anim.slide_out_right // Pop Exit animation (when going back)
                    )
                    .replace(R.id.frameLayout, tripImageFragment)
                    .addToBackStack(null) // Add to back stack
                    .commit()
            }
            }

    }




    private fun setupUI() {
        // Initialize Weekdays RecyclerView
        binding.weekdaysRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 7)
        binding.weekdaysRecyclerView.adapter = WeekdaysAdapter()

        // Initialize Calendar RecyclerView
        calendarAdapter = CalendarAdapter(days) { selectedDay ->
            handleDateSelection(selectedDay)
        }
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 7)
        binding.calendarRecyclerView.adapter = calendarAdapter

        // Set up month navigation
        updateMonthText()
        binding.btnLeftArrow.setOnClickListener {
            currentYearMonth = currentYearMonth.minusMonths(1)
            loadDaysForMonth(currentYearMonth)
        }
        binding.btnRightArrow.setOnClickListener {
            currentYearMonth = currentYearMonth.plusMonths(1)
            loadDaysForMonth(currentYearMonth)
        }
    }

    private fun loadDaysForMonth(yearMonth: YearMonth) {
        days.clear()
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Adjust Sunday as first day

        // Add empty slots for days before the 1st of the month
        for (i in 0 until firstDayOfWeek) {
            days.add(CalendarDay(LocalDate.MIN)) // Placeholder days
        }

        // Add actual days
        for (day in 1..daysInMonth) {
            days.add(CalendarDay(firstDayOfMonth.plusDays(day.toLong() - 1)))
        }

        calendarAdapter.notifyDataSetChanged()
        updateMonthText()
    }

    private fun updateMonthText() {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        binding.tvMonth.text = currentYearMonth.format(formatter)
    }

    private fun handleDateSelection(selectedDay: CalendarDay) {
        if (selectedStartDate == selectedDay.date || selectedEndDate == selectedDay.date) {
            resetSelection()
        } else if (selectedStartDate == null || (selectedStartDate != null && selectedEndDate != null)) {
            resetSelection()
            selectedStartDate = selectedDay.date
            selectedDay.isSelected = true
            selectedDay.isRangeStart = true

            binding.etFromDate.setText("${selectedStartDate.toString()}")
            binding.etToDate.setText("")



        } else if (selectedEndDate == null && selectedStartDate != null) {
            if (selectedDay.date.isBefore(selectedStartDate)) {
                resetSelection()
                selectedStartDate = selectedDay.date
                selectedDay.isSelected = true
                selectedDay.isRangeStart = true
                // Toast the new start date if it is reset


                binding.etFromDate.setText("${selectedStartDate.toString()}")
                binding.etToDate.setText("")


            } else {
                selectedEndDate = selectedDay.date
                selectedDay.isSelected = true
                selectedDay.isRangeEnd = true

                val rangeStart = selectedStartDate!!.toEpochDay()
                val rangeEnd = selectedEndDate!!.toEpochDay()
                days.forEach {
                    if (it.date.toEpochDay() in rangeStart..rangeEnd) {
                        it.isSelected = true
                        it.isRangeStart = it.date == selectedStartDate
                        it.isRangeEnd = it.date == selectedEndDate
                    }
                }

                binding.etToDate.setText("${selectedEndDate.toString()}")
                binding.btnContinue.isEnabled = true

            }
        }

        calendarAdapter.notifyDataSetChanged()
    }

    private fun resetSelection() {
        selectedStartDate = null
        selectedEndDate = null
        days.forEach {
            it.isSelected = false
            it.isRangeStart = false
            it.isRangeEnd = false
        }
    }

    class CalendarAdapter(
        private val days: List<CalendarDay>,
        private val onDateClick: (CalendarDay) -> Unit
    ) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
            val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DayViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            val day = days[position]
            holder.bind(day)
        }

        override fun getItemCount(): Int = days.size

        inner class DayViewHolder(private val binding: ItemCalendarDayBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(day: CalendarDay) {
                // Set the day number or an empty string if the date is LocalDate.MIN
                binding.dateTextView.text = if (day.date != LocalDate.MIN) day.date.dayOfMonth.toString() else ""

                // Resolve colors using ContextCompat
                val startColor = ContextCompat.getColor(binding.root.context, R.color.primary_pink)
                val endColor = ContextCompat.getColor(binding.root.context, R.color.primary_pink)
                val selectedColor = ContextCompat.getColor(binding.root.context, R.color.primary_light)
                val textColor = ContextCompat.getColor(binding.root.context, R.color.white)
                val transparentColor = Color.TRANSPARENT

                // Apply background colors based on the state of the day
                when {
                    binding.dateTextView.text.toString().isEmpty() -> {
                        // If the date is empty, set a transparent background and text color
                        binding.cvcard.setCardBackgroundColor(textColor)
                      // Optional: Set text color to white or any other color
                    }
                    day.isRangeStart -> {
                        // If the day is the range start, set the start color
                        binding.cvcard.setCardBackgroundColor(startColor)
                        binding.dateTextView.setTextColor(textColor) // Ensure text color is set for visibility
                    }
                    day.isRangeEnd -> {
                        // If the day is the range end, set the end color
                        binding.cvcard.setCardBackgroundColor(endColor)
                        binding.dateTextView.setTextColor(textColor) // Set text color to white
                    }
                    day.isSelected -> {
                        // If the day is selected, set the selected color
                        binding.cvcard.setCardBackgroundColor(selectedColor)
                        binding.dateTextView.setTextColor(textColor) // Ensure text color is set for visibility
                    }
                    else -> {
                        // Default: Set the background color to transparent
                        binding.cvcard.setCardBackgroundColor(textColor)
                        // Set text color to white
                    }
                }

                // Handle click on a valid day
                binding.root.setOnClickListener {
                    if (day.date != LocalDate.MIN) onDateClick(day)
                }
            }
        }


    }

    class WeekdaysAdapter : RecyclerView.Adapter<WeekdaysAdapter.WeekdayViewHolder>() {
        private val weekdays = listOf("S", "M", "T", "W", "T", "F", "S")
        private val textColor = Color.parseColor("#C73E70") // Change this to your desired color

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekdayViewHolder {
            val textView = LayoutInflater.from(parent.context).inflate(
                android.R.layout.simple_list_item_1, parent, false
            ) as TextView
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            return WeekdayViewHolder(textView)
        }

        override fun onBindViewHolder(holder: WeekdayViewHolder, position: Int) {
            val textView = holder.itemView as TextView
            textView.text = weekdays[position]
            textView.setTextColor(textColor) // Set the desired text color
        }

        override fun getItemCount(): Int = weekdays.size

        class WeekdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

}
