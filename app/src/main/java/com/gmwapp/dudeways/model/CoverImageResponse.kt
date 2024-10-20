package com.gmwapp.dudeways.model

data class CoverImageResponse(
    val success:Boolean,
    val message:String,
    val data:CoverImageModel
)
data class CoverImageModel(
    val cover_img:String
)