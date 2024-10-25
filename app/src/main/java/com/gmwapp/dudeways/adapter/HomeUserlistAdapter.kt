package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.activity.ChatsActivity
import com.gmwapp.dudeways.databinding.LayoutHomeProfilesBinding
import com.gmwapp.dudeways.databinding.LayoutHomeUserlistBinding
import com.gmwapp.dudeways.model.HomeUserlist
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class HomeUserlistAdapter(
    val activity: Activity,
    homeUserlist: java.util.ArrayList<HomeUserlist>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val homeUserlist: ArrayList<HomeUserlist>
    val activitys: Activity

    val session = Session(activity)

    init {
        this.homeUserlist = homeUserlist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutHomeUserlistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: HomeUserlist = homeUserlist[position]


        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)

        if (report.gender == "male") {
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)
        }
        else {
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)
        }

        holder.binding.ivProfile.setOnClickListener {
            if (report.id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.id)
                intent.putExtra("unique_name", report.unique_name)
                activity.startActivity(intent)
            }
        }
    }


    override fun getItemCount(): Int {
        return homeUserlist.size
    }

    internal class ItemHolder(val binding: LayoutHomeUserlistBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }


}