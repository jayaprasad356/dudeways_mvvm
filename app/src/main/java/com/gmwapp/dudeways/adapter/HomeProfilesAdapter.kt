package com.gmwapp.dudeways.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.New.UsersprofileDetailsActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.activity.ProfileInfoActivity
import com.gmwapp.dudeways.databinding.LayoutHomePostBinding
import com.gmwapp.dudeways.databinding.LayoutHomeProfilesBinding
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class HomeProfilesAdapter(
    val activity: Activity,
    private var homeProfile: ArrayList<HomeProfile>,
    private var onclick: onItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomePostBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: HomeProfile = homeProfile[position]

        val session = Session(activity)

        holder.binding.tvName.text = report.name
        holder.binding.tvLocation.text = report.location
//        holder.binding.tvDescription.text = report.trip_description
//        holder.binding.tvUsername.text = "@" + report.unique_name
        holder.binding.tvDate.text = "From " + report.from_date + " to " + report.to_date
        holder.binding.tvTitle.text = report.trip_title
        holder.binding.tvKm.text = "" + report.distance
        holder.binding.tvTripType.text = "" + report.trip_type
       // holder.binding.tvtime.text = "\u00B7 " + report.time
        holder.binding.tvtime.text = "" + report.time

//        holder.binding.tvUsername.text = if (report.name?.length ?: 0 > 7) {
//            if (report.unique_name?.length ?: 0 > 7) {
//                "@${report.unique_name?.substring(0, 7)}.."
//            } else {
//                "@${report.unique_name}"
//            }
//        } else {
//            "@${report.unique_name}"
//        }




//        holder.binding.tvmore.setOnClickListener {
//            if (holder.binding.tvDescription.visibility == View.VISIBLE) {
//                holder.binding.tvDescription.visibility = View.GONE
//                holder.binding.tvmore.text = activity.getString(R.string.more)
//            } else {
//                holder.binding.tvDescription.visibility = View.VISIBLE
//                holder.binding.tvmore.text = activity.getString(R.string.less)
//            }
//        }

        if (report.verified == "1") {
            holder.binding.ivVerify.visibility = View.VISIBLE
        } else {
            holder.binding.ivVerify.visibility = View.GONE
        }

        var friend_data: String = report.friend.toString()

        if (friend_data == "0") {
            holder.binding.ivaddFriend.setBackgroundResource(R.drawable.add_user)
            holder.binding.tvAddFriend.text = "Add to Friend"
        } else if (friend_data == "1") {
            holder.binding.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            holder.binding.tvAddFriend.text = "Friend Added"
        }

        holder.binding.rlAddFriend.setOnClickListener {
            if (friend_data == "0") {
                val friend = "1"
                friend_data = "1"
                onclick.onAddFriendClick(
                    holder.binding.ivaddFriend,
                    holder.binding.tvAddFriend, report.user_id.toString(), friend
                )

            } else if (friend_data == "1") {
                val friend = "2"
                friend_data = "0"
                onclick.onAddFriendClick(
                    holder.binding.ivaddFriend,
                    holder.binding.tvAddFriend, report.user_id.toString(), friend
                )
            }
        }
        holder.binding.ivProfile.setOnClickListener {
            val intent = Intent(activity, UsersprofileDetailsActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.user_id)
            intent.putExtra("id", report.id)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)
        }
        holder.binding.rlChat.setOnClickListener {
            if (report.user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.user_id)
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("friend_verified", report.verified)
                intent.putExtra("gender", report.gender)
                activity.startActivity(intent)
            }
        }
//        Glide.with(activity).load(report.trip_image).placeholder(R.drawable.placeholder_bg)
//            .into(holder.binding.ivProfileImage)

        Glide.with(activity)
            .asBitmap()
            .load(report.trip_image)
            .placeholder(R.drawable.placeholder_bg)
            .into(object :
                com.bumptech.glide.request.target.BitmapImageViewTarget(holder.binding.ivProfileImage) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    // Calculate the new dimensions based on the image aspect ratio
                    val width = resource.width / 1
                    val height = resource.height / 1

                    // Set the ImageView's dimensions
                    holder.binding.ivProfileImage.layoutParams.width = width
                    holder.binding.ivProfileImage.layoutParams.height = height
                    holder.binding.ivProfileImage.requestLayout()

                    // Set the loaded bitmap to the ImageView
                    holder.binding.ivProfileImage.setImageBitmap(resource)
                }
            })

        Glide.with(activity).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)
    }


    override fun getItemCount(): Int {
        return homeProfile.size
    }

    class ItemHolder(val binding: LayoutHomePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface onItemClick {
        fun onAddFriendClick(
            ivAddFriend: ImageView,
            tvAddFriend: TextView,
            userId: String,
            friends: String
        )


    }



}
