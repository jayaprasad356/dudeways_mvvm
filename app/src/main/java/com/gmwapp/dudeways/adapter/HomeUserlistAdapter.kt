package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
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

        holder.binding.tvName.text = report.name


        if (report.introduction!!.length == 0) {

        } else if (report.introduction!!.length > 45) {
            holder.binding.tvLatestseen.text = report.introduction!!.substring(0, 45) + ".."
        } else {
            holder.binding.tvLatestseen.text = report.introduction
        }


        val gender = report.gender


        if (gender == "male") {
            holder.binding.ivGender.setBackgroundDrawable(
                getTintedDrawable(activity, R.drawable.male_ic, R.color.blue_200)
            )
            holder.binding.tvAge.text = report.age
            holder.binding.tvAge.setTextColor(ContextCompat.getColor(activity, R.color.blue_200)) // Change text color
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.blue_200)
        } else {
            holder.binding.ivGender.setBackgroundDrawable(
                getTintedDrawable(activity, R.drawable.female_ic, R.color.primary_pink)
            )
            holder.binding.tvAge.text = report.age
            holder.binding.tvAge.setTextColor(ContextCompat.getColor(activity, R.color.primary_pink)) // Change text color
            holder.binding.ivProfile.borderColor = ContextCompat.getColor(activity, R.color.primary)
        }








        holder.binding.tvChat.setOnClickListener {
            if (report.id == session.getData(Constant.USER_ID)) {
                Toast.makeText(activity, "You can't chat with yourself", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(activity, ChatsActivity::class.java)
                intent.putExtra("id", report.id)
                intent.putExtra("name", report.name)
                session.setData("reciver_profile", report.profile)
                intent.putExtra("chat_user_id", report.id)
                intent.putExtra("unique_name", report.unique_name)
                intent.putExtra("gender", report.gender)
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
    // Helper function to get tinted drawable
    private fun getTintedDrawable(activity: Activity, drawableRes: Int, colorRes: Int): Drawable {
        val drawable = ContextCompat.getDrawable(activity, drawableRes)!!
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(activity, colorRes))
        return wrappedDrawable
    }

}