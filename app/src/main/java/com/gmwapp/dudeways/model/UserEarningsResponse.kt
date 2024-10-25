package com.gmwapp.dudeways.model

import com.google.gson.annotations.SerializedName

data class UserEarningsResponse(
    val success: Boolean,
    val message: String,

    @SerializedName("selfi_image_url")
    var selfieImageUrl: String,

    @SerializedName("proof_image_url")
    var proofImageUrl: String
)





