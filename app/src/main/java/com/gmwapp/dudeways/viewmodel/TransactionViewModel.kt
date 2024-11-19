package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.NotificationResponse
import com.gmwapp.dudeways.model.TransactionResponse
import com.gmwapp.dudeways.repositories.NotificationRepositories
import com.gmwapp.dudeways.repositories.TransactionRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(val transactionRepositories: TransactionRepositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val transactionLiveData = MutableLiveData<TransactionResponse>()

    fun getTransaction(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            transactionRepositories.getTransaction(userId).let {
                if (it.body() != null) {
                    transactionLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }




}