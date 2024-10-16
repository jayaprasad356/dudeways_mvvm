package com.gmwapp.dudeways.model

data class ProffessionResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<ProffessionModel>
)

data class ProffessionModel(
    val profession: String,
    val id: String
)
