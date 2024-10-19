package com.gmwapp.dudeways.model

import com.google.android.gms.ads.rewarded.RewardedAd

data class RewardResponse(
    val success: Boolean,
    val message: String,
    val data: RewardModel
)

data class RewardModel(
    val points: String
)