package com.demo.repositoriesviewer.presentation

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.AppRepositoryImpl

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepositoryImpl = AppRepositoryImpl(application)

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    val token = MutableLiveData<String>()
    // val actions: Flow<Action>

    private val sharedPreferences =
        application.getSharedPreferences(NAME_SHARED_PREFERENCE, MODE_PRIVATE)
    val keyValueStorage = repository.keyValueStorage

    fun onSignButtonPressed() {
        if (token.value.isNullOrBlank()) {
            _state.value = State.InvalidInput(
                getApplication<Application>()
                    .getString(R.string.value_not_entered)
            )
            return
        }
        _state.value = State.Loading
        // try{Action.RouteToMain}

//        repository.keyValueStorage.authToken?.let {
//            viewModelScope.launch {
//                repository.signIn(it)
//            }
//        }

        // catch{ShowError(e.message)}
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: String) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }

    fun saveToken(newToken: String?) {
        token.value = newToken
        keyValueStorage.authToken = newToken
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE, newToken).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE, "token.value")
    }

    companion object {
        const val NAME_SHARED_PREFERENCE = "shared_preference"
        const val KEY_SHARED_PREFERENCE = "token_value"
    }
}