package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private var _isToken = MutableLiveData<Boolean>()
    val isToken: LiveData<Boolean>
        get() = _isToken

    fun startMainActivity() {
        viewModelScope.launch {
            val token = appRepository.getToken()
            _isToken.value = !token.isNullOrBlank()
        }
    }
}