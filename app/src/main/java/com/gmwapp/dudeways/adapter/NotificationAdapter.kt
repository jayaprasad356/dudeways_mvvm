package com.gmwapp.dudeways.adapter

import android.app.Activity
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
import com.gmwapp.dudeways.databinding.LayoutHomeNotificationsBinding
import com.gmwapp.dudeways.model.NotificationModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationAdapter(
    val activity: Activity,
    notification: ArrayList<NotificationModel>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        // Get the current notification's date
        val currentDate = report.date?.let { getFormattedDate(it) }

        // Determine if the date header should be displayed
        val shouldShowDateHeader = report.date?.let { shouldDisplayDateHeader(position, it) }

        // Handle the date header visibility
        if (currentDate != null) {
            if (shouldShowDateHeader != null) {
                handleDateHeader(holder.binding.tvDateHeader, shouldShowDateHeader, currentDate)
            }
        }

        if (report.gender == "male") {
            holder.binding.civProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)
        } else {
            holder.binding.civProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)
        }

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.civProfile)

        if (report.verified == "1") {
            holder.binding.ivVerify.visibility = View.VISIBLE
        } else {
            holder.binding.ivVerify.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if (report.notify_user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
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

    internal class ItemHolder(val binding: LayoutHomeNotificationsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun shouldDisplayDateHeader(position: Int, currentDate: String): Boolean {
        if (position == 0) return true // Always show the date header for the first item
        val previousNotification = notification[position - 1]
        val previousDate = previousNotification.date?.let { getFormattedDate(it) }
        val currentFormattedDate = getFormattedDate(currentDate)
        return currentFormattedDate != previousDate
    }

    private fun handleDateHeader(dateHeader: TextView, shouldShow: Boolean, date: String) {
        if (shouldShow) {
            dateHeader.text = date
            dateHeader.visibility = View.VISIBLE
        } else {
            dateHeader.visibility = View.GONE
        }
    }

    private fun getFormattedDate(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val notificationDate = sdf.parse(dateString)
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            notificationDate != null && isSameDay(today, notificationDate) -> "Today"
            notificationDate != null && isSameDay(yesterday, notificationDate) -> "Yesterday"
            else -> dateString // Use original date if not today or yesterday
        }
    }

    private fun isSameDay(calendar: Calendar, date: Date): Boolean {
        val target = Calendar.getInstance().apply { time = date }
        return calendar.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
