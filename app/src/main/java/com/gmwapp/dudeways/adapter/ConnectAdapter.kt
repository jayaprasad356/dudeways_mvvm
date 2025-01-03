package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.New.UsersprofileDetailsActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.model.ConnectModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.AddFriendViewModel

class ConnectAdapter(
    private val activity: Activity,
    private val connectList: ArrayList<ConnectModel>,
    private val viewModel: AddFriendViewModel
) : RecyclerView.Adapter<ConnectAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = LayoutHomeConnectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val report = connectList[position]
        val session = Session(activity)

        // Set verification visibility
        holder.binding.ivVerify.visibility = if (report.verified == "1") View.VISIBLE else View.GONE

        // Set age and gender-specific styles
        holder.binding.tvAge.text = report.age
        val genderColor = if (report.gender == "male") R.color.blue_200 else R.color.primary_pink
        val genderIcon = if (report.gender == "male") R.drawable.male_ic else R.drawable.female_ic

        holder.binding.ivGender.setBackgroundDrawable(getTintedDrawable(activity, genderIcon, genderColor))
        holder.binding.tvAge.setTextColor(ContextCompat.getColor(activity, genderColor))
        holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, genderColor)

        // Set profile image
        Glide.with(activity)
            .load(report.profile)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)

        // Handle introduction text
        holder.binding.tvLanguage.text = report.language
        // Handle profile click
        holder.binding.ivProfile.setOnClickListener {
            val intent = Intent(activity, UsersprofileDetailsActivity::class.java).apply {
                putExtra("name", report.name)
                putExtra("chat_user_id", report.friend_user_id)
                putExtra("id", report.id)
                putExtra("friend", report.friend)
                session.setData("reciver_profile", report.profile)
            }
            activity.startActivity(intent)
        }

        // Handle remove friend action
        holder.binding.tvRemove.setOnClickListener {
            viewModel.addFriend(
                session.getData(Constant.USER_ID).toString(),
                report.friend_user_id.toString(),
                "2"
            )

            // Remove the item from the list
            connectList.removeAt(position)

            // Notify the adapter about the item removed
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, connectList.size)

            // Optional: Show a toast or other feedback
            Toast.makeText(activity, "${report.name} removed friend", Toast.LENGTH_SHORT).show()
        }


        // Set name
        holder.binding.tvName.text = report.name
    }

    override fun getItemCount(): Int = connectList.size

    inner class ItemHolder(val binding: LayoutHomeConnectBinding) : RecyclerView.ViewHolder(binding.root)

    // Helper function to get tinted drawable
    private fun getTintedDrawable(activity: Activity, drawableRes: Int, colorRes: Int): Drawable {
        val drawable = ContextCompat.getDrawable(activity, drawableRes)!!
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(activity, colorRes))
        return wrappedDrawable
    }
}
