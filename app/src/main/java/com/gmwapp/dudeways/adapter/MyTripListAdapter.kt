package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.LayoutMytipListBinding
import com.gmwapp.dudeways.model.MyTripModel

class MyTripListAdapter(
    val activity: Activity,
    mytriplist: java.util.ArrayList<MyTripModel>,
    val onclick:onItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val mytriplist: ArrayList<MyTripModel>
    val activitys: Activity

    init {
        this.mytriplist = mytriplist
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(
            LayoutMytipListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder: ItemHolder = holderParent as ItemHolder
        val report: MyTripModel = mytriplist[position]




        holder.binding.tvName.text = report.name
        holder.binding.tvLocation.text = report.location
        holder.binding.tvDescription.text = report.trip_description
        holder.binding.tvUsername.text = "@" + report.unique_name
        holder.binding.tvDate.text = "From " + report.from_date + " to " + report.to_date
        holder.binding.tvTitle.text = report.trip_title


        // check report.user_name is more than 10 latters
        if (report.name?.length!! > 10) {
            if (report.unique_name?.length!! > 7) {
                holder.binding.tvUsername.text = "@" + report.unique_name!!.substring(0, 7) + ".."
            } else {
                holder.binding.tvUsername.text = "@" + report.unique_name
            }
        } else {
            holder.binding.tvUsername.text = "@" + report.unique_name
        }

        if (report.trip_status == "0") {
            holder.binding.tvStatus.text = "In Review"
            holder.binding.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.yellow))
            holder.binding.tvStatus.setIconResource(R.drawable.panding_clock)
            holder.binding.tvStatus.iconTint =
                ColorStateList.valueOf(activity.resources.getColor(R.color.black))
        } else if (report.trip_status == "1") {
            holder.binding.tvStatus.text = "Approved"
            holder.binding.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.green))
            holder.binding.tvStatus.setIconResource(R.drawable.verified_icon)
            holder.binding.tvStatus.iconTint =
                ColorStateList.valueOf(activity.resources.getColor(R.color.white))
        } else if (report.trip_status == "2") {
            holder.binding.tvStatus.text = "Rejected"
            holder.binding.tvStatus.setBackgroundColor(activity.resources.getColor(R.color.red))
            holder.binding.tvStatus.setIconResource(R.drawable.rejected_icon)
            holder.binding.tvStatus.iconTint =
                ColorStateList.valueOf(activity.resources.getColor(R.color.white))
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



        holder.binding.llDelete.setOnClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Delete Trip")
                .setMessage("Are you sure you want to delete this trip?")
                .setPositiveButton("Yes") { dialog, which ->
                    onclick.onDeleteClick(tripId = report.id.toString(),position)
                   // deleteTrip(report.id)
                }
                .setNegativeButton("No", null)
                .show()
        }


        // Load the image and adjust the height based on its aspect ratio
        // Load the image and adjust the height based on its aspect ratio
        Glide.with(activitys)
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

        Glide.with(activitys).load(report.profile).placeholder(R.drawable.profile_placeholder)
            .into(holder.binding.ivProfile)


    }


    override fun getItemCount(): Int {
        return mytriplist.size
    }

    internal class ItemHolder(val binding: LayoutMytipListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    interface onItemClick{
        fun onDeleteClick(tripId:String,position: Int)
    }
}