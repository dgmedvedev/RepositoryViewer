package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    init {
        val token = appRepository.getToken() ?: EMPTY_TOKEN
        _token.value = token
    }

    fun onSignButtonPressed(token: String?) {
        viewModelScope.launch {
            token?.let { token ->
                _state.value = State.Loading
                if (tokenIsValid(newToken = token)) {
                    try {
                        appRepository.signIn(token = token)
                        _token.value = token
                        appRepository.saveToken(newToken = token)
                        _actions.send(Action.RouteToMain)
                    } catch (e: RuntimeException) {
                        _actions.send(Action.ShowError(message = e.message.toString()))
                    }
                    _state.value = State.Idle
                }
            }
        }
    }

    private fun tokenIsValid(newToken: String?): Boolean {
        if (newToken.isNullOrBlank()) {
            _state.value = State.InvalidInput(reason = R.string.value_not_entered)
            return false
        }
        if (Pattern.matches(IN_CYRILLIC, newToken)) {
            _state.value = State.InvalidInput(reason = R.string.value_invalid)
            return false
        }
        if (!Pattern.matches(NOT_CHAR, newToken)) {
            _state.value = State.InvalidInput(reason = R.string.unexpected_char)
            return false
        }
        return true
    }

    private companion object {
        const val IN_CYRILLIC = ".*\\p{InCyrillic}.*"
        const val NOT_CHAR = "^\\w*\$"
        const val EMPTY_TOKEN = ""
    }

    sealed interface State {
        object Idle : State
        object Loading : State
        data class InvalidInput(val reason: Int) : State
    }

    sealed interface Action {
        data class ShowError(val message: String) : Action
        object RouteToMain : Action
    }
}