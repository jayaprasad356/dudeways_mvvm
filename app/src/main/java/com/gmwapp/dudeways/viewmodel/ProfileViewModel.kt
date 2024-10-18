package com.gmwapp.dudeways.viewmodel

import androidx.lifecycle.ViewModel
import com.gmwapp.dudeways.repositories.ProfileRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(val profileRepositories: ProfileRepositories):ViewModel() {
}