package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.NotificationResponse
import com.gmwapp.dudeways.repositories.EarningRepositories
import com.gmwapp.dudeways.repositories.NotificationRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarningViewModel @Inject constructor(val earningRepositories: EarningRepositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val updateMobileLiveData = MutableLiveData<BaseResponse>()



    fun updateMobileData(userId: String, mobile:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            earningRepositories.updateMobile(userId, mobile).let {
                if (it.body() != null) {
                    updateMobileLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



}