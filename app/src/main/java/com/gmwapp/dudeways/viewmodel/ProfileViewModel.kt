package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.CoverImageResponse
import com.gmwapp.dudeways.model.PrivarcyPolicyResponse
import com.gmwapp.dudeways.model.RefundPolicyResponse
import com.gmwapp.dudeways.model.TermsConditionResponse
import com.gmwapp.dudeways.repositories.ProfileRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val profileRepositories: ProfileRepositories) :
    ViewModel() {


    val isLoading = MutableLiveData(false)
    val deleteAccountLiveData = MutableLiveData<BaseResponse>()
    val addFeedBackLiveData = MutableLiveData<BaseResponse>()
    val termsConditionLiveData = MutableLiveData<TermsConditionResponse>()
    val refundPolicyLiveData = MutableLiveData<RefundPolicyResponse>()
    val privarcyPolicyLiveData = MutableLiveData<PrivarcyPolicyResponse>()
    val coverImageLiveData = MutableLiveData<CoverImageResponse>()

    fun deleteAccount(userId: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.deleteAccount(userId).let {
                if (it.body() != null) {
                    deleteAccountLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun addFeedBack(userId: String, feedBack: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.addFeedBack(userId, feedBack).let {
                if (it.body() != null) {
                    addFeedBackLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun termsCondition() {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.getTermsCondiitons().let {
                if (it.body() != null) {
                    termsConditionLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun getRefundPolicy() {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.getRefundPolciy().let {
                if (it.body() != null) {
                    refundPolicyLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



    fun getPrivarcyPolicy() {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.getPrivarcyPolicy().let {
                if (it.body() != null) {
                    privarcyPolicyLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun getCoverImage(uid:RequestBody,image:MultipartBody.Part?) {
        viewModelScope.launch {
            isLoading.postValue(true)
            profileRepositories.getCoverImage(uid,image).let {
                if (it.body() != null) {
                    coverImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}