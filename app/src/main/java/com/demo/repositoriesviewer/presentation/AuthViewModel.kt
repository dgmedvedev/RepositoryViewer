package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.usecases.GetKeyValueStorageUseCase
import com.demo.repositoriesviewer.domain.usecases.SaveKeyValueStorageUseCase
import com.demo.repositoriesviewer.domain.usecases.SignInUseCase
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
    getKeyValueStorageUseCase: GetKeyValueStorageUseCase,
    private val saveKeyValueStorageUseCase: SaveKeyValueStorageUseCase,
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> = _token

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    init {
        val keyValueStorage = getKeyValueStorageUseCase()
        _token.value = keyValueStorage.authToken
    }

    fun onSignButtonPressed(token: String) {
        viewModelScope.launch {
            if (isInternetAvailable()) {
                _state.value = State.Loading
                if (tokenIsValid(newToken = token)) {
                    try {
                        signInUseCase(token = token)
                        _token.value = token
                        val updateKeyValueStorage = KeyValue(token)
                        saveKeyValueStorageUseCase(keyValue = updateKeyValueStorage)
                        _actions.send(Action.RouteToMain)
                    } catch (e: RuntimeException) {
                        _actions.send(Action.ShowError(message = e.message.toString()))
                    }
                    _state.value = State.Idle
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
        const val INTERNET_ACCESS_ERROR = "internet_access_error"
        const val UNEXPECTED_CHAR = "Unexpected char"
        const val VALUE_INVALID = "value_invalid"
        const val VALUE_IS_EMPTY = ""
        const val VALUE_NOT_ENTERED = "value_not_entered"
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