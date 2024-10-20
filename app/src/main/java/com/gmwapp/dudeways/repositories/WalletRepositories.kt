package com.gmwapp.dudeways.repositories

import com.gmwapp.dudeways.network.ApiService
import javax.inject.Inject

class WalletRepositories @Inject constructor(val apiService: ApiService) {

    suspend fun getWithdraw(userId: String) = apiService.getWithdraw(userId)

    suspend fun getWallet(userId: String, amount: String) = apiService.withdraw(
        userId, amount
    )

    suspend fun updateBank(
        userId: String, name: String, number: String,
        ifsc: String, bankName: String, branchName: String
    ) = apiService.updateBank(
        userId, name, number, ifsc, bankName, branchName
    )
}