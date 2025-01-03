package com.gmwapp.dudeways.New.Fragment

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.FragmentSelectLanguageBinding
import com.google.android.material.card.MaterialCardView
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar

class SelectLanguageFragment : Fragment() {

    private lateinit var binding: FragmentSelectLanguageBinding
    private var mobileNumber: String? = null

    data class Language(
        val nameNative: String,   // Language in native text (e.g., "தமிழ்")
        val nameEnglish: String,  // Language in English (e.g., "Tamil")
        var isSelected: Boolean = false
    )

    private val languages = listOf(
        Language("தமிழ்", "Tamil"),
        Language("తెలుగు", "Telugu"),
        Language("മലയാളം", "Malayalam"),
        Language("ಕನ್ನಡ", "Kannada"),
        Language("हिंदी", "Hindi"),
        Language("বাংলা", "Bengali"),
        Language("ગુજરાતી", "Gujarati"),
        Language("मराठी", "Marathi"),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_language, container, false)
        mobileNumber = arguments?.getString("mobile_number") ?: ""

        addListener()
        setupRecyclerView()
        return binding.root
    }

    private fun addListener() {
        binding.btnDone.setOnClickListener {
            val adapter = binding.recyclerLanguage.adapter as? LanguageAdapter
            val selectedPosition = adapter?.getSelectedPosition() ?: -1

            if (selectedPosition == -1) {
                // No language selected
                showSnackbar("Please select a language")
            } else {
                // Language selected
                val selectedLanguage = adapter!!.languages[selectedPosition]

                // Create a Bundle to pass data to the next fragment
                val bundle = Bundle()
                bundle.putString("language", selectedLanguage.nameEnglish)
                bundle.putString("mobile_number", mobileNumber)

                // Navigate to SetProfileFragment with slide animation and pass the bundle
                val setProfileFragment = SetProfileFragment()
                setProfileFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.slide_in_right, // Enter animation
                        R.anim.slide_out_left, // Exit animation
                        R.anim.slide_in_left,  // Pop Enter animation
                        R.anim.slide_out_right // Pop Exit animation
                    )
                    .replace(R.id.fragment_container, setProfileFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = LanguageAdapter(
            languages,
            { selectedLanguage ->
                // Log or toast the selection for debugging if needed
            },
            { isEnabled ->
                binding.btnDone.isEnabled = isEnabled // Enable or disable the button
            }
        )
        binding.recyclerLanguage.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerLanguage.adapter = adapter
        binding.btnDone.isEnabled = false // Initially disabled
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    class LanguageAdapter(
        val languages: List<Language>,
        private val onItemSelected: (Language) -> Unit,
        private val onSelectionChange: (Boolean) -> Unit
    ) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

        private var selectedPosition = -1

        inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cardView: MaterialCardView = itemView.findViewById(R.id.card_language)
            val nativeText: TextView = itemView.findViewById(R.id.tv_language_native)
            val englishText: TextView = itemView.findViewById(R.id.tv_language_english)
            val checkIcon: ImageView = itemView.findViewById(R.id.ivCheck)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_language, parent, false)
            return LanguageViewHolder(view)
        }

        override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
            val language = languages[position]

            holder.nativeText.text = language.nameNative // e.g., "தமிழ்"
            holder.englishText.text = language.nameEnglish // e.g., "Tamil"

            // Update UI based on selection
            holder.cardView.strokeColor = if (selectedPosition == position) {
                ContextCompat.getColor(holder.itemView.context, R.color.primary_pink)
            } else {
                ContextCompat.getColor(holder.itemView.context, R.color.grey)
            }

            holder.checkIcon.visibility = if (selectedPosition == position) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                // Notify the fragment to enable the button
                onSelectionChange(true)
                onItemSelected(language)
            }
        }

        override fun getItemCount(): Int = languages.size

        fun getSelectedPosition(): Int = selectedPosition
    }
}
