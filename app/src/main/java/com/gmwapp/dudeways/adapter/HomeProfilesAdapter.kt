package com.gmwapp.dudeways.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ProfessionAdapter.ProfessionViewHolder
import com.gmwapp.dudeways.databinding.ItemProfessionBinding
import com.gmwapp.dudeways.databinding.LayoutHomeProfilesBinding
import com.gmwapp.dudeways.model.HomeProfile
import com.gmwapp.dudeways.utils.Session

class HomeProfilesAdapter(
    val activity: Activity,
    private var homeProfile: ArrayList<HomeProfile>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomeProfilesBinding.inflate(
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
        holder.binding.tvDescription.text = report.trip_description
        holder.binding.tvUsername.text = "@" + report.unique_name
        holder.binding.tvDate.text = "From " + report.from_date + " to " + report.to_date
        holder.binding.tvTitle.text = report.trip_title
        holder.binding.tvKm.text = "" + report.distance
        holder.binding.tvtime.text = "\u00B7 " + report.time

        if (report.name?.length!! > 10) {
            if (report.unique_name?.length!! > 7) {
                holder.binding.tvUsername.text = "@" + report.unique_name!!.substring(0, 7) + ".."
            } else {
                holder.binding.tvUsername.text = "@" + report.unique_name
            }
        } else {
            holder.binding.tvUsername.text = "@" + report.unique_name
        }

        holder.binding.tvmore.setOnClickListener {
            if (holder.binding.tvDescription.visibility == View.VISIBLE) {
                holder.binding.tvDescription.visibility = View.GONE
                holder.binding.tvmore.text = activity.getString(R.string.more)
            } else {
                holder.binding.tvDescription.visibility = View.VISIBLE
                holder.binding.tvmore.text = activity.getString(R.string.less)
            }
        }

//        if (report.verified == "1") {
//            holder.ivVerify.visibility = View.VISIBLE
//        } else {
//            holder.ivVerify.visibility = View.GONE
//        }

        var friend_data: String = report.friend.toString()

        if (friend_data == "0") {
            holder.binding.ivaddFriend.setBackgroundResource(R.drawable.add_account)
            holder.binding.tvAddFriend.text = "Add to Friend"
        } else if (friend_data == "1") {
            holder.binding.ivaddFriend.setBackgroundResource(R.drawable.added_frd)
            holder.binding.tvAddFriend.text = "Friend Added"
        }

     /*   holder.rlAddFriend.setOnClickListener {
            if (friend_data == "0") {
                val friend = "1"
                friend_data = "1"
                add_friend(holder.ivaddFriend, holder.tvAddFriend, report.user_id, friend)
            } else if (friend_data == "1") {
                val friend = "2"
                friend_data = "0"
                add_friend(holder.ivaddFriend, holder.tvAddFriend, report.user_id, friend)
            }
        }
*/
      /*  holder.llProfile.setOnClickListener {
            val intent = Intent(activity, ProfileinfoActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.user_id)
            intent.putExtra("id", report.id)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)
        }
*/
  /*      holder.rlChat.setOnClickListener {
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
                activity.startActivity(intent)
            }
        }
*/
        Glide.with(activity).load(report.trip_image).placeholder(R.drawable.placeholder_bg)
            .into(holder.binding.ivProfileImage)

        Glide.with(activity).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)
    }

    override fun getItemCount(): Int {
        return homeProfile.size
    }

    class ItemHolder(val binding: LayoutHomeProfilesBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}
