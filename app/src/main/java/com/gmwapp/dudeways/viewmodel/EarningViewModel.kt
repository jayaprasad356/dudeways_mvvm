package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.UserEarningsResponse
import com.gmwapp.dudeways.repositories.EarningRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EarningViewModel @Inject constructor(val earningRepositories: EarningRepositories) :
    ViewModel() {

    val isLoading = MutableLiveData(false)
    val updateMobileLiveData = MutableLiveData<BaseResponse>()
    val earningImageLiveData = MutableLiveData<UserEarningsResponse>()
    val withOutVerificationLiveData = MutableLiveData<BaseResponse>()



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

    fun doUpdateImage(
        userId: RequestBody,
        type:RequestBody, selfi_image: MultipartBody.Part?, proof_image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            earningRepositories.updateImage(userId, type, selfi_image, proof_image).let {
                if (it.body() != null) {
                    earningImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


 fun doWithOutVerification(
        userId: String,
        type:String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            earningRepositories.updateWithOutVerification(userId, type).let {
                if (it.body() != null) {
                    withOutVerificationLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }




}