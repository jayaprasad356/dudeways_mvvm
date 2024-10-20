package com.gmwapp.dudeways.model

data class FontImageResponse(
    val success:Boolean,
    val message:String,
    val data:FontImageModel
)
data class FontImageModel(
    val front_image:String
)