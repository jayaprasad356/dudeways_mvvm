package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.RegisterResponse
import com.gmwapp.dudeways.repositories.RegisterRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val registerRepositories: RegisterRepositories):ViewModel() {
    val isLoading = MutableLiveData(false)
    val registerLiveData = MutableLiveData<RegisterResponse>()
    val updateImageLiveData = MutableLiveData<RegisterResponse>()

    fun doRegister(name: String,email:String,age:String,gender:String,
                   proffessionId:String,
                   state:String,
                   city:String,
                   introduction:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            registerRepositories.doRegister(name,email,age,gender,proffessionId, state, city, introduction).let {
                if (it.body() != null) {
                    registerLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


    fun doUpdateImage(userId:String,filePath:String) {
        viewModelScope.launch {
            isLoading.postValue(true)
            registerRepositories.updateImage(userId,filePath).let {
                if (it.body() != null) {
                    registerLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}