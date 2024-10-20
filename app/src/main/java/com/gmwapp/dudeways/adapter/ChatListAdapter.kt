package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.activity.ProfileInfoActivity
import com.gmwapp.dudeways.adapter.ConnectAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutChatListBinding
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.model.ChatModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class ChatListAdapter(
    val activity: Activity,
    chatlist: java.util.ArrayList<ChatModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val chatlist: ArrayList<ChatModel>
    val activitys: Activity

    init {
        this.chatlist = chatlist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutChatListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: ChatModel = chatlist[position]
        val session = Session(activity)


        val unread = report.unread


        //holder.tvCount.text = "Points "+report.points


        if (unread == "0") {
            holder.binding.tvUnread.visibility = View.GONE
        } else {
            holder.binding.tvUnread.visibility = View.VISIBLE
            holder.binding.tvUnread.text = unread
        }


        holder.binding.TVUserName.text = report.name
        holder.binding.TVMessageContent.text = report.latest_message

//        if (report.verified == "1") {
//            holder.ivVerify.visibility = View.VISIBLE
//        } else {
//            holder.ivVerify.visibility = View.GONE
//        }

        if (report.online_status == "1") {
            holder.binding.IVOnlineStatus.visibility = View.VISIBLE
        } else {
            holder.binding.IVOnlineStatus.visibility = View.GONE
        }



        holder.binding.TVSentTime.text = report.latest_msg_time

        holder.binding.IVUserProfile.setOnClickListener {
            val intent = Intent(activity, ProfileInfoActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.chat_user_id)
            intent.putExtra("id", report.id)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)

        }
        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.IVUserProfile)


        val point = session.getData(Constant.POINTS)

        holder.itemView.setOnClickListener {


            if (report.chat_user_id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                //        chatList.clear()
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.chat_user_id)
                intent.putExtra("unread", report.unread)
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("friend_verified", report.verified)
                session.setData(Constant.MSG_SEEN, report.msg_seen)
                activity.startActivity(intent)
            }


        }


    }


    override fun getItemCount(): Int {
        return chatlist.size
    }

    internal class ItemHolder(val binding: LayoutChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}