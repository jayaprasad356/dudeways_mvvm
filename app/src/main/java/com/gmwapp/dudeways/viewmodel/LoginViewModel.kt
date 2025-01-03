package com.gmwapp.dudeways.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.AppUpdateResponse
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.model.VoiceUpdateResponse
import com.gmwapp.dudeways.repositories.LoginRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginRepositories: LoginRepositories) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val loginLiveData = MutableLiveData<LoginResponse>()
    val loginMobileLiveData = MutableLiveData<LoginResponse>()
    val proffessionLiveData = MutableLiveData<ProffessionResponse>()
    val appUpdateLiveData = MutableLiveData<AppUpdateResponse>()
    val voiceUpdateLiveData = MutableLiveData<VoiceUpdateResponse>()

    fun doLogin(email: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            loginRepositories.checkEmail(email).let {
                if (it.body() != null) {
                    loginLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



    fun addVoiceTest(userId: RequestBody, voiceFilePath: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            loginRepositories.addVoice(userId, voiceFilePath).let {
                if (it.body() != null) {
                    voiceUpdateLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun doLoginMobile(mobile: String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            loginRepositories.checkMobile(mobile).let {
                if (it.body() != null) {
                    loginMobileLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun appUpdate() {
        viewModelScope.launch {
            isLoading.postValue(true)
            loginRepositories.appUpdate().let {
                if (it.body() != null) {
                    appUpdateLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }



    fun getProffession() {
        viewModelScope.launch {
            isLoading.postValue(true)
            loginRepositories.getProffession().let {
                if (it.body() != null) {
                    proffessionLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }







}