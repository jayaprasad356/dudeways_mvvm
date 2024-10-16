package com.gmwapp.dudeways.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.gmwapp.dudeways.R
import com.bumptech.glide.Glide

class ViewPagerAdapter(private val context: Context) : PagerAdapter() {
    private val images = intArrayOf(
        R.drawable.welcome1,
        R.drawable.welcome2,
        R.drawable.welcome3
    )

    private val headings = intArrayOf(
        R.string.splash1,
        R.string.splash2,
        R.string.splash3
    )

    override fun getCount(): Int {
        return images.size + 1 // Add one more count for the dummy page
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater = LayoutInflater.from(context)

        if (position == images.size) {
            // This is the dummy page
            val view: View = layoutInflater.inflate(R.layout.slider_dummy_layout, container, false)
            container.addView(view)
            return view
        } else {
            val view: View = layoutInflater.inflate(R.layout.slider_layout, container, false)

            val slideTitleImage = view.findViewById<ImageView>(R.id.ivSplashImage)
            val slideHeading = view.findViewById<TextView>(R.id.tvSplashText)

            Glide.with(context).load(images[position]).placeholder(R.drawable.welcome1)
                .into(slideTitleImage)
            slideHeading.setText(headings[position])

            container.addView(view)
            return view
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
