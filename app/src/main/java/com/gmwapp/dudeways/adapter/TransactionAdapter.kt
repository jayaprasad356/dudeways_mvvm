package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.app.Notification
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.adapter.ConnectAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.databinding.LayoutHomeNotificationsBinding
import com.gmwapp.dudeways.databinding.LayoutHomeTransactionBinding
import com.gmwapp.dudeways.model.NotificationModel
import com.gmwapp.dudeways.model.TransactionModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session


class TransactionAdapter(
    val activity: Activity,
    transaction: java.util.ArrayList<TransactionModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val transaction: ArrayList<TransactionModel>
    val activitys: Activity

    init {
        this.transaction = transaction
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomeTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: TransactionModel = transaction[position]
        val session = Session(activity)


        holder.binding.tvtype.text = report.type
        holder.binding.tvpoints.text = report.points.toString()
        holder.binding.tvdatetime.text = report.datetime









    }


    override fun getItemCount(): Int {
        return transaction.size
    }

    internal class ItemHolder(val binding: LayoutHomeTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
    }


}