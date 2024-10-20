package com.gmwapp.dudeways.model

import java.sql.Ref

data class RefundPolicyResponse(
    val success: Boolean,
    val message: String,
    val data: ArrayList<RefundPolicyModel>
)

data class RefundPolicyModel(
    val refund_policy: String
)