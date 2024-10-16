package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.adapter.ViewPagerAdapter
import com.gmwapp.dudeways.databinding.ActivityWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityWelcomeBinding
    lateinit var mContext: WelcomeActivity
    private var dots: Array<TextView?> = arrayOf()
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        mContext = this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
        addListner()
    }

    private fun initUI() {


        viewPagerAdapter = ViewPagerAdapter(this)
        binding.slideViewPager.adapter = viewPagerAdapter

        setUpIndicator(0)
        binding.slideViewPager.addOnPageChangeListener(viewListener)


    }

    private fun addListner() {
        binding.tvSkip.setOnClickListener {
            startActivity(Intent(mContext, LoginActivity::class.java))
            finish()
        }

    }

    private fun setUpIndicator(position: Int) {
        dots = arrayOfNulls(4) // Change to 4 to include the dummy page
        binding.indicatorLayout.removeAllViews()
        for (i in 0 until dots.size - 1) { // Adjust to skip the dummy page in the indicator
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226")
            dots[i]?.textSize = 55f
            dots[i]?.setTextColor(resources.getColor(R.color.inactive, theme))
            binding.indicatorLayout.addView(dots[i])
        }
        if (position < dots.size - 1) {
            dots[position]?.setTextColor(resources.getColor(R.color.active, theme))
        }
    }

    private val viewListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            setUpIndicator(position)
            if (position == dots.size - 1) { // Check if the dummy page is reached
                startActivity(Intent(mContext, LoginActivity::class.java))
                finish()
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

}