package com.gmwapp.dudeways.model

data class TransactionResponse(
    val success: Boolean,
    val message: String,
    val total: String,
    val data: ArrayList<TransactionModel>
)

data class TransactionModel(
    var id: Int? = 0,
    var user_id: Int? = 0,
    var points: Int? = 0,
    var amount: String? = "",
    var type: String? = "",
    var datetime: String? = ""

)
