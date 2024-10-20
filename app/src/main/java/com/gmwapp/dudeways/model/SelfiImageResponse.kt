package com.gmwapp.dudeways.model

data class SelfiImageResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<SelfiImageModel>
)

data class SelfiImageModel(
    val selfie_image: String
)