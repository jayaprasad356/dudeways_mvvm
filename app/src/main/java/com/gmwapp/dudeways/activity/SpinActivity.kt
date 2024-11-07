package com.gmwapp.dudeways.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.gmwapp.dudeways.R
import com.gmwapp.dudeways.databinding.ActivitySpinBinding
import com.gmwapp.dudeways.extentions.isNetworkAvailable
import com.gmwapp.dudeways.utils.Constant
import com.gmwapp.dudeways.utils.Session
import com.gmwapp.dudeways.viewmodel.ChatViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import java.util.Random

@AndroidEntryPoint
class SpinActivity : BaseActivity() {

    lateinit var binding: ActivitySpinBinding
    lateinit var mContext: SpinActivity
    private lateinit var session: Session

    private val sectors = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    private val sectorDegrees = IntArray(sectors.size)
    private var isSpinning = false
    private val viewModel: ChatViewModel by viewModels()
    private var rewardedAd: RewardedAd? = null
    private val adId = "ca-app-pub-8693482193769963/5956761344"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spin)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_spin)
        mContext = this
        initUI()
        addListner()
        addObsereves()
    }

    private fun initUI() {
        session = Session(mContext)

        degreeForSectors()

        loadRewardedVideoAd()


    }

    private fun addListner() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.spinBtn.setOnClickListener {
            if (!isSpinning) {
                spin()
                isSpinning = true
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

        viewModel.spinPointsLiveData.observe(this, Observer {
            if (it.success) {
                showRewardedVideoAd()
                session.setData(Constant.POINTS, it.data.points)
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun spin() {
        // Set the degree to stop at sector 6
        val degreeToStop = 10 // Index 5 corresponds to sector 6

        // Calculate the final rotation degree based on the selected sector
        val rotateDegrees = 360 * 5 + sectorDegrees[degreeToStop]

        val rotateAnimation = RotateAnimation(
            0f, rotateDegrees.toFloat(),
            RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 3600
        rotateAnimation.fillAfter = true
        rotateAnimation.interpolator = DecelerateInterpolator()
        rotateAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isSpinning = true
            }

            override fun onAnimationEnd(animation: Animation) {
                isSpinning = false

                // Process the sector based on its value (sector 6 in this case)
                val sectorValue = sectors[degreeToStop]
                addPurchase(sectorValue)
            }

            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        binding.wheel1?.startAnimation(rotateAnimation)
    }

    private fun degreeForSectors() {
        val sectorDegree = 360 / sectors.size
        for (i in sectors.indices) {
            sectorDegrees[i] = (i + 1) * sectorDegree
        }
    }

    private fun addPurchase(point: String) {
        var points = ""
        when (point) {
//            "1" -> points = "20"
//            "2" -> points = "30"
//            "3" -> points = "40"
//            "4" -> points = "50"
//            "5" -> points = "100"
//            "6" -> points = "10"
//
            "1" -> points = "10"
            "2" -> points = "20"
            "3" -> points = "30"
            "4" -> points = "40"
            "5" -> points = "50"
            "6" -> points = "100"

        }
        if (isNetworkAvailable(mContext)) {
            viewModel.spinPoints(session.getData(Constant.USER_ID).toString(), "10")
        } else {
            Toast.makeText(
                mContext,
                getString(R.string.str_error_internet_connections),
                Toast.LENGTH_SHORT
            ).show()
        }
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

                viewModel.spinPointsLiveData.observe(this, Observer {
                    if (it.success) {
                        session.setData(Constant.POINTS, it.data.points)
                        Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mContext, it.message, Toast.LENGTH_SHORT).show()

                    }
                })

            }
        } ?: run {
            loadRewardedVideoAd()
        }
    }

}