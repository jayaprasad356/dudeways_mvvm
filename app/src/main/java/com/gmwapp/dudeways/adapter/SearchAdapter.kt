package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.New.UsersprofileDetailsActivity
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.activity.ProfileInfoActivity
import com.gmwapp.dudeways.adapter.ConnectAdapter.ItemHolder
import com.gmwapp.dudeways.databinding.LayoutHomeConnectBinding
import com.gmwapp.dudeways.model.SearchModel
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session


class SearchAdapter(
    val activity: Activity,
    usersList: java.util.ArrayList<SearchModel>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val usersList: ArrayList<SearchModel>
    val activitys: Activity

    init {
        this.usersList = usersList
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
        val report: SearchModel = usersList[position]

        val session = Session(activity)

//        holder.binding.IVOnlineStatus.visibility = View.GONE

        holder.binding.tvAge.text = report.age
//        holder.tvDistance.text = report.distance


        if (report.verified == "1") {
            holder.binding.ivVerify.visibility = View.VISIBLE
        } else {
            holder.binding.ivVerify.visibility = View.GONE
        }



        val gender = report.gender

        if (gender == "male") {
            holder.binding.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.male_ic))
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)

        } else {
            holder.binding.ivGender.setBackgroundDrawable(activity.resources.getDrawable(R.drawable.female_ic))
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)

        }

        if (gender == "male") {
            holder.binding.ivGenderColor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.blue_200))
        } else {
            holder.binding.ivGenderColor.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.primary))
        }


        /*  holder.itemView.setOnClickListener{
              val intent = Intent(activity, ProfileinfoActivity::class.java)
              activity.startActivity(intent)
          }
  */


        val point = session.getData(Constant.POINTS)
        val userId = session.getData(Constant.USER_ID)


        holder.itemView.setOnClickListener {

            Log.d("id", "chat_user_id " + report.id)
            Log.d("id", "chat_user_id name " + report.name)
            Log.d("id", "chat_user_id profile " + report.profile)
            Log.d("id", "chat_user_id unique_name " + report.unique_name)
            Log.d("id", "chat_user_id verified " + report.verified)

            if (report.id.toString() == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.id.toString())
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("friend_verified", report.verified)
                activity.startActivity(intent)
            }


        }

        holder.binding.ivProfile.setOnClickListener {
            val intent = Intent(activity, UsersprofileDetailsActivity::class.java)
            intent.putExtra("name", report.name)
            intent.putExtra("chat_user_id", report.id.toString())
            intent.putExtra("id", userId)
            session.setData("reciver_profile", report.profile)
            intent.putExtra("friend", report.friend)
            activity.startActivity(intent)

        }

        holder.binding.tvName.text = report.name


        //holder.tvLatestseen.text = report.introduction is more than one line mean en with dot



     //       holder.binding.tvLanguage.text = report.language



        Glide.with(activitys)
            .load(report.profile)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)


    }


    override fun getItemCount(): Int {
        return usersList.size
    }

    internal class ItemHolder(val binding: LayoutHomeConnectBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }


}