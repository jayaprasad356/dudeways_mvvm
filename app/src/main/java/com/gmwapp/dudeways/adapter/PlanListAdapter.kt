package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import com.gmwapp.dudeways.databinding.LayoutPlanlistBinding

import androidx.recyclerview.widget.RecyclerView
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ConnectAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.model.PlanListModel

class PlanListAdapter(
    val activity: Activity,
    private val planList: ArrayList<PlanListModel>,
    private val onclick:onItemClick,
    private val onPlanSelected: (PlanListModel) -> Unit
) : RecyclerView.Adapter<PlanListAdapter.ItemHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            LayoutPlanlistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val plan = planList[position]

        holder.binding.tvSave.text = "Save Rs." + plan.save_amount
        holder.binding.tvPlan.text = plan.plan_name
        holder.binding.tvDays.text = " / ${plan.validity} Days"
        holder.binding.tvPlanPrice.text = "Rs.${plan.price}"

        holder.binding.rbplan.visibility = View.GONE

        // On plan item click, handle selection and initiate payment
        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onPlanSelected(plan)
            //initiatePaymentLink()
        }
    }

    override fun getItemCount(): Int {
        return planList.size
    }

    private fun getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    inner class ItemHolder(val binding:LayoutPlanlistBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    interface onItemClick{
        fun onClick()
    }
}