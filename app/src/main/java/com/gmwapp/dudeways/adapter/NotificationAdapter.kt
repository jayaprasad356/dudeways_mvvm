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
import com.gmwapp.dudeways.model.NotificationModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session


class NotificationAdapter(
    val activity: Activity,
    notification: java.util.ArrayList<NotificationModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val notification: ArrayList<NotificationModel>
    val activitys: Activity

    init {
        this.notification = notification
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomeNotificationsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: NotificationModel = notification[position]
        val session = Session(activity)


        holder.binding.tvName.text = report.name
        holder.binding.tvMessage.text = report.message
        holder.binding.tvtime.text = report.time

        if (report.gender == "male") {
            holder.binding.civProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)
        }
        else {
            holder.binding.civProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)
        }

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.civProfile)

//        holder.civProfile.setOnClickListener {
//            val intent = Intent(activity, ProfileinfoActivity::class.java)
//            intent.putExtra("name", report.name)
//            intent.putExtra("chat_user_id", report.notify_user_id.toString())
//            intent.putExtra("id", report.id.toString())
//            session.setData("reciver_profile", report.profile)
//            activity.startActivity(intent)
//
//        }

//        if (report.verified == "1") {
//            holder.ivVerify.visibility = View.VISIBLE
//        } else {
//            holder.ivVerify.visibility = View.GONE
//        }


        holder.itemView.setOnClickListener{

            if (report.notify_user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.notify_user_id)
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("friend_verified", report.verified)
                activity.startActivity(intent)
            }



        }

    }


    override fun getItemCount(): Int {
        return notification.size
    }

    internal class ItemHolder(val binding:LayoutHomeNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {
    }


}