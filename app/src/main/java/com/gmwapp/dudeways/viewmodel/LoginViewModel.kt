package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.AppUpdateResponse
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.ProffessionResponse
import com.gmwapp.dudeways.repositories.LoginRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val loginRepositories: LoginRepositories) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val loginLiveData = MutableLiveData<LoginResponse>()
    val proffessionLiveData = MutableLiveData<ProffessionResponse>()
    val appUpdateLiveData = MutableLiveData<AppUpdateResponse>()

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