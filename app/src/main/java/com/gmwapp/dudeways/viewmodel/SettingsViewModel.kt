package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmwapp.dudeways.model.BaseResponse
import com.gmwapp.dudeways.model.SettingsResponse
import com.gmwapp.dudeways.repositories.SettingsRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(val settingsRepositories: SettingsRepositories) :
    ViewModel() {
    val isLoading = MutableLiveData(false)
    val settingLiveData = MutableLiveData<SettingsResponse>()

    fun getSettings() {
        viewModelScope.launch {
            isLoading.postValue(true)
            settingsRepositories.getSettings().let {
                if (it.body() != null) {
                    settingLiveData.postValue(it.body())
                    isLoading.postValue(false)
                } else {
                    isLoading.postValue(false)
                }
            }

        }
    }

}