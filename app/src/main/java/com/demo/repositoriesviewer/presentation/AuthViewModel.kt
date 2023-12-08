package com.demo.repositoriesviewer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.demo.repositoriesviewer.R

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    val token = MutableLiveData<String>()
    //val actions: Flow<Action>

    fun onSignButtonPressed() {
        if (token.value.isNullOrBlank()) {
            _state.value = State.InvalidInput(
                getApplication<Application>()
                    .getString(R.string.value_not_entered)
            )
            return
        }
        _state.value = State.Loading
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

    // TODO:
}