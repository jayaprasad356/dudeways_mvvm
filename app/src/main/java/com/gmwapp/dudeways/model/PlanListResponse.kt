package com.gmwapp.dudeways.model

data class PlanListResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<PlanListModel>
)

data class PlanListModel(
    var id: String? = "",
    var plan_name: String? = "",
    var validity: String? = "",
    var price: String? = "",
    var save_amount: String? = "",

    )