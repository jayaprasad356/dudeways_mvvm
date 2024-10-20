package com.gmwapp.dudeways.model

data class PurchaseResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<PurchaseModel>
)

data class PurchaseModel(
    val id: String,
    val points: String,
    val offer_percentage: String,
    val price: String,
    val datetime: String,
    val updated_at: String,
    val created_at: String
)