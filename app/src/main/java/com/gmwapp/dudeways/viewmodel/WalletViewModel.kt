package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.WalletResponse
import com.gmwapp.dudeways.model.WithdrawResponse
import com.gmwapp.dudeways.repositories.WalletRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(val walletRepositories: WalletRepositories)
    :ViewModel(){

    val isLoading = MutableLiveData(false)
    val walletLiveData = MutableLiveData<WithdrawResponse>()
    val withdrawLiveData = MutableLiveData<WalletResponse>()
    val updateBankLiveData = MutableLiveData<BaseResponse>()

    fun getWallet(userId:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            walletRepositories.getWithdraw(userId).let {
                if (it.body() != null) {
                    walletLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getWithdraw(userId:String,amount:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            walletRepositories.getWallet(userId,amount).let {
                if (it.body() != null) {
                    withdrawLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun updateBank( userId: String, name: String, number: String,
                    ifsc: String, bankName: String, branchName: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            walletRepositories.updateBank(userId,name,number,ifsc, bankName, branchName).let {
                if (it.body() != null) {
                    updateBankLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}