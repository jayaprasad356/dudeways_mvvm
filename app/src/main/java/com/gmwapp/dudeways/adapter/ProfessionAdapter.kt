package com.gmwapp.dudeways.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ItemProfessionBinding

class ProfessionAdapter(
    private val professions: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ProfessionAdapter.ProfessionViewHolder>() {

    class ProfessionViewHolder(val binding: ItemProfessionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionViewHolder {
        return ProfessionViewHolder(
            ItemProfessionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfessionViewHolder, position: Int) {
        val profession = professions[position]
        holder.binding.professionTextView.setText(professions[position])
        holder.itemView.setOnClickListener {
            onItemClick(profession)
        }
    }

    override fun getItemCount(): Int {
        return professions.size
    }
}
