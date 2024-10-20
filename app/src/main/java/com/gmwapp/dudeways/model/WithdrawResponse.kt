package com.gmwapp.dudeways.model

data class WithdrawResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<WithdrawModel>
)

data class WithdrawModel(
    val id: String,
    val user_id: String,
    val amount: String,
    val datetime: String,
    val status: Int
)