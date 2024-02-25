package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.usecases.GetTokenUseCase
import com.demo.repositoriesviewer.domain.usecases.SaveTokenUseCase
import com.demo.repositoriesviewer.domain.usecases.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getTokenUseCase: GetTokenUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    val token = MutableLiveData<String>()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    fun getToken(): String? {
        return getTokenUseCase().authToken
    }

    fun saveToken(newToken: String?) {
        val keyValue = KeyValue(authToken = newToken)
        saveTokenUseCase(keyValue = keyValue)
    }

    fun onSignButtonPressed() {
        viewModelScope.launch {
            if (isInternetAvailable()) {
                _state.value = State.Loading
                val keyValueStorage = getTokenUseCase()
                val enteredToken = keyValueStorage.authToken ?: VALUE_IS_EMPTY
                if (tokenIsValid(enteredToken)) {
                    try {
                        signInUseCase(enteredToken)
                        delay(500)
                        token.value = enteredToken
                        val updateKeyValueStorage = KeyValue(enteredToken)
                        saveTokenUseCase(updateKeyValueStorage)
                        _actions.send(Action.RouteToMain)
                    } catch (e: RuntimeException) {
                        _actions.send(Action.ShowError(e.message.toString()))
                    }
                    _state.value = State.Idle
                }
            } else {
                _actions.send(
                    Action.ShowError(
                        "Check your internet connection"
//                        getApplication<Application>().getString(R.string.internet_access_error)
                    )
                )
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
            _state.value = State.InvalidInput(
                "Enter your personal access token"
//                getApplication<Application>()
//                    .getString(R.string.value_not_entered)
            )
            return false
        }
        if (Pattern.matches(IN_CYRILLIC, newToken)) {
            _state.value = State.InvalidInput(
                "Invalid token"
//                getApplication<Application>()
//                    .getString(R.string.value_invalid)
            )
            return false
        }
        return true
    }

    companion object {
        const val AVAILABLE_ADDRESS = "api.github.com"
        const val IN_CYRILLIC = ".*\\p{InCyrillic}.*"
        const val VALUE_IS_EMPTY = ""
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