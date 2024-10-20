package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ConnectAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.databinding.LayoutWalletBinding
import com.gmwapp.dudeways.model.WithdrawModel

class WithdrawalAdapter(
    val activity: Activity,
    val withdrawalList: ArrayList<WithdrawModel> // Use the correct type here
) : RecyclerView.Adapter<WithdrawalAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            LayoutWalletBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val report: WithdrawModel = withdrawalList[position]
        holder.binding.tvPrice.text = "₹${report.amount.toString()}"
        holder.binding.tvDateTime.text = report.datetime

        if (report.status == 0) {
            holder.binding.tvTitle.text = "Pending"
            holder.binding.tvTitle.setTextColor(activity.resources.getColor(R.color.primary))
            holder.binding.civProfile.setImageResource(R.drawable.pending_img)
        } else if (report.status == 1) {
            holder.binding.tvTitle.text = "Success"
//            holder.tvTitle.setTextColor(activity.resources.getColor(R.color.success_color))
            holder.binding.civProfile.setImageResource(R.drawable.done_ic)
        } else if (report.status == 2) {
            holder.binding.tvTitle.text = "Cancel"
//            holder.tvTitle.setTextColor(activity.resources.getColor(R.color.success_color))
            holder.binding.civProfile.setImageResource(R.drawable.failed_ic)
        }
    }

    override fun getItemCount(): Int {
        return withdrawalList.size
    }

    inner class ItemHolder(val binding:LayoutWalletBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}

