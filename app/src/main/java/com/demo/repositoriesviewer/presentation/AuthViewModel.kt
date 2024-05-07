package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
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
        val token = appRepository.getToken() ?: VALUE_IS_EMPTY
        _token.value = token
        if (token.isNotBlank()) {
            onSignButtonPressed(token = token)
        }
    }

    fun onSignButtonPressed(token: String?) {
        viewModelScope.launch {
            if (isInternetAvailable()) {
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
            } else {
                _actions.send(Action.ShowError(message = INTERNET_ACCESS_ERROR))
            }
        }
    }

    private suspend fun isInternetAvailable(): Boolean {
        return try {
            val ipAddress: InetAddress = withContext(Dispatchers.IO) {
                InetAddress.getByName(AVAILABLE_ADDRESS)
            }
            !ipAddress.equals(VALUE_IS_EMPTY)
        } catch (e: Exception) {
            false
        }
    }

    private fun tokenIsValid(newToken: String?): Boolean {
        if (newToken.isNullOrBlank()) {
            _state.value = State.InvalidInput(reason = VALUE_NOT_ENTERED)
            return false
        }
        if (Pattern.matches(IN_CYRILLIC, newToken)) {
            _state.value = State.InvalidInput(reason = VALUE_INVALID)
            return false
        }
        if (!Pattern.matches(NOT_CHAR, newToken)) {
            _state.value = State.InvalidInput(reason = UNEXPECTED_CHAR)
            return false
        }
        return true
    }

    companion object {
        const val HTTP_401_ERROR = "HTTP 401 "
        const val HTTP_403_ERROR = "HTTP 403 "
        const val HTTP_404_ERROR = "HTTP 404 "
        const val HTTP_422_ERROR = "HTTP 422 "
        const val AVAILABLE_ADDRESS = "api.github.com"
        const val NOT_CHAR = "^\\w*\$"
        const val IN_CYRILLIC = ".*\\p{InCyrillic}.*"
        const val INTERNET_ACCESS_ERROR = "Internet access error"
        const val UNEXPECTED_CHAR = "Unexpected char"
        const val VALUE_INVALID = "Value invalid"
        const val VALUE_IS_EMPTY = ""
        const val VALUE_NOT_ENTERED = "Value is not entered"
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