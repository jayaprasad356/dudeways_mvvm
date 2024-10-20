package com.gmwapp.dudeways.model

data class BackImageResponse(
    val success: Boolean,
    val message: String,
    val data: BackImageModel
)

data class BackImageModel(
    val back_image: String
)
