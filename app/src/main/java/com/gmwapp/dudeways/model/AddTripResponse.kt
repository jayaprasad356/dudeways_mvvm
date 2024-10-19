package com.gmwapp.dudeways.model

data class AddTripResponse(
    val success: Boolean,
    val message: String,
    val data: AddTripModel
)

data class AddTripModel(
    val id: String
)