package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivityFreePointsBinding
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreePointsActivity : AppCompatActivity() {

    lateinit var binding: ActivityFreePointsBinding
    lateinit var mContext: FreePointsActivity

    private lateinit var session: Session
    private var rewardedAd: RewardedAd? = null
    private val adId = "ca-app-pub-8693482193769963/5956761344"
    private val viewModel: ChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_free_points)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)
        MobileAds.initialize(this) {}

        loadRewardedVideoAd()

    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.llStep2.setOnClickListener {
            val intent = Intent(mContext, SpinActivity::class.java)
            startActivity(intent)
        }

        binding.llStep4.setOnClickListener {
            val intent = Intent(mContext, InviteFriendsActivity::class.java)
            startActivity(intent)
        }

        binding.llStep3.setOnClickListener {
            // Check if the ad is loaded before allowing the user to watch it
            if (rewardedAd != null) {
                showRewardedVideoAd()
            } else {
                loadRewardedVideoAd()
                showRewardedVideoAd()
            }
        }
    }

    private fun addObsereves() {
        viewModel.isLoading.observe(this, Observer {
            if (it) {
                binding.pbLoadData.visibility = View.VISIBLE
            } else {
                binding.pbLoadData.visibility = View.GONE
            }
        })

        viewModel.addPurchaseLiveData.observe(this, Observer {
            if (it.success) {
                session.setData(Constant.POINTS, it.data.points)
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadRewardedVideoAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, adId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                rewardedAd = null
                //  Toast.makeText(this@FreePointsActivity, "Ad Failed To Load", Toast.LENGTH_SHORT).show()
            }

            override fun onAdLoaded(ad: RewardedAd) {
                rewardedAd = ad
                //  Toast.makeText(this@FreePointsActivity, "Ad Loaded", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRewardedVideoAd() {
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem: RewardItem ->
                viewModel.addPurchase(
                    session.getData(Constant.USER_ID).toString(),
                    "1"
                )
                Toast.makeText(this, "Video Completed", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            loadRewardedVideoAd()
        }
    }

}