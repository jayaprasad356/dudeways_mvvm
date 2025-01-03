package com.gmwapp.dudeways.New.Post

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.New.Fragment.MobileLoginFragment
import com.gmwapp.dudeways.New.Fragment.OtpPageFragment
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentPlanningPostBinding
import com.google.android.material.card.MaterialCardView

class PlanningPostFragment : Fragment() {

    private lateinit var binding: FragmentPlanningPostBinding
    private lateinit var activity: Activity

    var TripName: String? = null

    data class Trip(
        val nameTrip: String,
        val ivIcon: Int,
        var isSelected: Boolean = false
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_planning_post, container, false)
        activity = requireActivity()
        addListner()

        setupRecyclerView()
        return binding.root
    }

    private fun addListner() {
        binding.btnContinue.setOnClickListener {
            // Validate if a trip is selected
            if (TripName.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select a trip before continuing.", Toast.LENGTH_SHORT).show()
            }

            else {
                // Create a Bundle to pass data to the next fragment
                val bundle = Bundle()
                bundle.putString("nameTrip", TripName)


                // Navigate to TripDetailsFragment with slide animation and pass the bundle
                val tripDetailsFragment = TripDetailsFragment()
                tripDetailsFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right, // Enter animation
                        R.anim.slide_out_left, // Exit animation
                        R.anim.slide_in_left,  // Pop Enter animation (when coming back)
                        R.anim.slide_out_right // Pop Exit animation (when going back)
                    )
                    .replace(R.id.frameLayout, tripDetailsFragment)
                    .addToBackStack(null) // Add to back stack
                    .commit()
            }

            }

    }


    private fun setupRecyclerView() {
        val trips = listOf(
            Trip("Mountains", R.drawable.ic_mountains),
            Trip("Club, Party", R.drawable.ic_party),
            Trip("Mall", R.drawable.ic_mall),
            Trip("Beach", R.drawable.ic_beach),
            Trip("Holy Places", R.drawable.ic_holy_places),
            Trip("Concert", R.drawable.ic_concert),
            Trip("Movies", R.drawable.ic_movies),
            Trip("Road Trips", R.drawable.ic_road_trips),
            Trip("Shopping", R.drawable.ic_shopping),
            Trip("Restaurant", R.drawable.ic_restaurant),
            Trip("Outing", R.drawable.ic_outing),
            Trip("Others", R.drawable.ic_others)
        )

        val adapter = TripAdapter(trips) { selectedTrip ->
          //  Toast.makeText(requireContext(), "Selected: ${selectedTrip.nameTrip}", Toast.LENGTH_SHORT).show()

             TripName = selectedTrip.nameTrip
            binding.btnContinue.isEnabled = true
        }

        binding.rvtrip.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvtrip.adapter = adapter
    }

    class TripAdapter(
        private val trips: List<Trip>,
        private val onItemSelected: (Trip) -> Unit
    ) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

        private var selectedPosition = -1

        inner class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: MaterialCardView = itemView.findViewById(R.id.card_trip)
            val tripName: TextView = itemView.findViewById(R.id.tv_trip_name)
            val tripIcon: ImageView = itemView.findViewById(R.id.iv_trip_icon) // Add an ImageView for icons
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_trip_planing, parent, false)
            return TripViewHolder(view)
        }

        override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
            val trip = trips[position]

            holder.tripName.text = trip.nameTrip
            holder.tripIcon.setImageResource(trip.ivIcon) // Set the icon for the trip

            // Update card appearance based on selection
            if (selectedPosition == position) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary_pink))
                holder.tripIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tripName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tripIcon.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.primary_pink))
                holder.tripName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
            }

            holder.cardView.strokeColor = ContextCompat.getColor(holder.itemView.context, R.color.white)

            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemSelected(trip)
            }
        }

        override fun getItemCount(): Int = trips.size
    }



}
