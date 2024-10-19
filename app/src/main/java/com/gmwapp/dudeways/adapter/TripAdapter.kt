package com.gmwapp.dudeways.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session

class TripAdapter(private val activity: Activity, private val list: ArrayList<String>) :
    RecyclerView.Adapter<TripAdapter.ItemHolder>() {

    var selectedItemPosition = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        return ItemHolder(v)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val itemHolder = holder
        val image = list[position].toInt()
        var session = Session(activity)
        Glide.with(activity).load(image).into(itemHolder.ivImage)
        if (position == selectedItemPosition) {
            itemHolder.ivCheck.visibility = View.VISIBLE
            session.setData(Constant.TRIP_TYPE, position.toString())
        } else {
            itemHolder.ivCheck.visibility = View.GONE
        }

        itemHolder.itemView.setOnClickListener {
            val previousSelectedPosition = selectedItemPosition
            selectedItemPosition = holder.adapterPosition
            //Toast.makeText(activity, "Selected: $position  ${session.getData(Constant.TRIP_TYPE)}", Toast.LENGTH_SHORT).show()
            session.setData(Constant.TRIP_TYPE, position.toString())
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(holder.adapterPosition)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        var ivCheck: ImageView = itemView.findViewById(R.id.ivCheck)
    }
}
