package com.gmwapp.dudeways.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.LoginResponse
import com.gmwapp.dudeways.model.RegisterNewResponse
import com.gmwapp.dudeways.model.RegisterResponse
import com.gmwapp.dudeways.repositories.RegisterRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(val registerRepositories: RegisterRepositories) :
    ViewModel() {
    val isLoading = MutableLiveData(false)
    val registerLiveData = MutableLiveData<RegisterResponse>()
    val profileLiveData = MutableLiveData<BaseResponse>()
    val registernewLiveData = MutableLiveData<RegisterNewResponse>()
    val updateImageLiveData = MutableLiveData<RegisterResponse>()

    fun doRegister(
        mobile : String,
        dob : String,
        name: String,
        email: String,
        age: String,
        gender: String,
        proffessionId: String,
        state: String,
        city: String,
        introduction: String,
        language:String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            registerRepositories.doRegister(
                mobile,
                dob,
                name,
                email,
                age,
                gender,
                proffessionId,
                state,
                city,
                introduction,language
            ).let {
                if (it.body() != null) {
                    registerLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

    fun doUpdateProfile(
        user_id : String,
        name : String
    ) {
        viewModelScope.launch {
            isLoading.postValue(true)
            registerRepositories.doUpdateProfile(
                user_id,
                name.toString(),
                ).let {
                if (it.body() != null) {
                    profileLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


//    fun donewRegister(
//        mobile: String,
//        name: String, dob: String, gender: String,
//        language:String
//    ) {
//        viewModelScope.launch {
//            isLoading.postValue(true)
//            registerRepositories.doRegisternew(
//                mobile,
//                name,
//                dob,
//                language,
//                gender
//            ).let {
//                if (it.body() != null) {
//                    registernewLiveData.postValue(it.body())
//                    isLoading.postValue(false)
//                } else {
//                    isLoading.postValue(false)
//                }
//            }
//
//        }
//    }


    fun doUpdateImage(userId: RequestBody, filePath: MultipartBody.Part?) {
        Log.d("doUpdateImage", "userId: $userId")
        Log.d("doUpdateImage", "filePath: $filePath")
        viewModelScope.launch {
            isLoading.postValue(true)
            registerRepositories.updateImage(userId, filePath).let {
                if (it.body() != null) {
                    updateImageLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }


}