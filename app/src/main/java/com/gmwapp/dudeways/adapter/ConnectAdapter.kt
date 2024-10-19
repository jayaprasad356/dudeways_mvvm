package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.HomeProfilesAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.databinding.LayoutHomeProfilesBinding
import com.gmwapp.dudeways.model.ConnectModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class ConnectAdapter(
    val activity: Activity,
    connect: java.util.ArrayList<ConnectModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val connect: ArrayList<ConnectModel>
    val activitys: Activity

    init {
        this.connect = connect
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomeConnectBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: ConnectModel = connect[position]

        val session = Session(activity)

        if (report.online_status == "1") {
            holder.binding.IVOnlineStatus.visibility = View.VISIBLE
        } else {
            holder.binding.IVOnlineStatus.visibility = View.GONE
        }

        val online_status = report.online_status
        var status = ""
        if (online_status == "1") {
            status = "Online"
        } else {
            status = ""
        }
//        if (report.verified == "1") {
//            holder.ivVerify.visibility = View.VISIBLE
//        } else {
//            holder.ivVerify.visibility = View.GONE
//        }

        holder.binding.tvAge.text = report.age
        holder.binding.tvDistance.text = report.distance


        val gender = report.gender

        if (gender == "male") {
            holder.binding.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.male_ic))
        } else {
            holder.binding.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.female_ic))
        }

        if (gender == "male") {
            holder.binding.ivGenderColor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.blue_200))
        } else {
            holder.binding.ivGenderColor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.primary))
        }


//        holder.itemView.setOnClickListener{
//            val intent = Intent(activity, ProfileinfoActivity::class.java)
//            activity.startActivity(intent)
//        }


        val point = session.getData(Constant.POINTS)

        /*

                holder.itemView.setOnClickListener {

                    Log.d("id", "chat_user_id " + report.id)
                    Log.d("id", "chat_user_id name " + report.name)
                    Log.d("id", "chat_user_id profile " + report.profile)
                    Log.d("id", "chat_user_id unique_name " + report.unique_name)
                    Log.d("id", "chat_user_id verified " + report.verified)

                    if (report.friend_user_id == session.getData(Constant.USER_ID)) {
                        Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(activity, ChatsActivity::class.java)
                        intent.putExtra("id", report.id)
                        intent.putExtra("name", report.name)
                        session.setData("reciver_profile", report.profile)
                        intent.putExtra("chat_user_id", report.friend_user_id)
                        intent.putExtra("unique_name", report.unique_name)
                        intent.putExtra("friend_verified", report.verified)
                        activity.startActivity(intent)
                    }


                }

                holder.ivProfile.setOnClickListener {
                    val intent = Intent(activity, ProfileinfoActivity::class.java)
                    intent.putExtra("name", report.name)
                    intent.putExtra("chat_user_id", report.friend_user_id)
                    intent.putExtra("id", report.id)
                    session.setData("reciver_profile", report.profile)
                    intent.putExtra("friend", report.friend)
                    activity.startActivity(intent)

                }
        */


        holder.binding.tvName.text = report.name


        //holder.tvLatestseen.text = report.introduction is more than one line mean en with dot

        if (report.introduction!!.length == 0) {

        } else if (report.introduction!!.length > 45) {
            holder.binding.tvLatestseen.text = report.introduction!!.substring(0, 45) + ".."
        } else {
            holder.binding.tvLatestseen.text = report.introduction
        }



        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)


    }


    override fun getItemCount(): Int {
        return connect.size
    }

    internal class ItemHolder(val binding: LayoutHomeConnectBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }


}