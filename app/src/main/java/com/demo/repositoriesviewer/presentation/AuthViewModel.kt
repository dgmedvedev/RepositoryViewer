package com.demo.repositoriesviewer.presentation

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.KeyValueStorage
import com.demo.repositoriesviewer.domain.usecases.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    @ApplicationContext application: Application,
    private val signInUseCase: SignInUseCase,
    private val keyValueStorage: KeyValueStorage
) : AndroidViewModel(application) {

    val token = MutableLiveData<String>()
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private val sharedPreferences =
        application.getSharedPreferences(NAME_SHARED_PREFERENCE, MODE_PRIVATE)

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE, VALUE_IS_EMPTY)
    }

    fun onSignButtonPressed() {
        viewModelScope.launch {
            if (isInternetAvailable()) {
                _state.value = State.Loading
                val enteredToken = keyValueStorage.authToken ?: VALUE_IS_EMPTY
                if (tokenIsValid(enteredToken)) {
                    try {
                        signInUseCase(enteredToken)
                        delay(500)
                        token.value = enteredToken
                        saveToken(enteredToken)
                        _actions.send(Action.RouteToMain)
                    } catch (e: RuntimeException) {
                        _actions.send(Action.ShowError(e.message.toString()))
                    }
                    _state.value = State.Idle
                }
            } else {
                _actions.send(
                    Action.ShowError(
                        getApplication<Application>().getString(R.string.internet_access_error)
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
        if (Pattern.matches(IN_CYRILLIC, newToken)) {
            _state.value = State.InvalidInput(
                getApplication<Application>()
                    .getString(R.string.value_invalid)
            )
            return false
        }
        return true
    }

    companion object {
        const val AVAILABLE_ADDRESS = "api.github.com"
        const val IN_CYRILLIC = ".*\\p{InCyrillic}.*"
        const val NAME_SHARED_PREFERENCE = "shared_preference"
        const val KEY_SHARED_PREFERENCE = "token_value"
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