package com.demo.repositoriesviewer.presentation

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val repository: AppRepositoryImpl = AppRepositoryImpl(application)

    val token = MutableLiveData<String>()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private val sharedPreferences =
        application.getSharedPreferences(NAME_SHARED_PREFERENCE, MODE_PRIVATE)

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE, "")
    }

    fun onSignButtonPressed() {
        val enteredToken = repository.keyValueStorage.authToken ?: TOKEN_IS_BLANK
        if (tokenIsValid(enteredToken)) {
            _state.value = State.Loading
            viewModelScope.launch {
                try {
                    repository.signIn(enteredToken)
                    token.value = enteredToken
                    saveToken(enteredToken)
                    _actions.send(Action.RouteToMain)
                } catch (e: RuntimeException) {
                    _actions.send(Action.ShowError(e.message.toString()))
                }
                delay(500)
                _state.value = State.Idle
            }
        }
    }

    private fun saveToken(newToken: String?) {
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE, newToken).apply()
    }

    private fun tokenIsValid(newToken: String?): Boolean {
        if (newToken.isNullOrBlank()) {
            _state.value = State.InvalidInput(
                getApplication<Application>()
                    .getString(R.string.value_not_entered)
            )
            return false
        }
        if (Pattern.matches(".*\\p{InCyrillic}.*", newToken)) {
            _state.value = State.InvalidInput(
                getApplication<Application>()
                    .getString(R.string.value_invalid)
            )
            return false
        }
        return true
    }

    companion object {
        const val NAME_SHARED_PREFERENCE = "shared_preference"
        const val KEY_SHARED_PREFERENCE = "token_value"
        const val TOKEN_IS_BLANK = ""
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
}