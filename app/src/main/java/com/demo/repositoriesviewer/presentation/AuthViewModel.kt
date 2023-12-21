package com.demo.repositoriesviewer.presentation

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepositoryImpl = AppRepositoryImpl(application)

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state
    val token = MutableLiveData<String>()
    // val actions: Flow<Action>

    private val sharedPreferences =
        application.getSharedPreferences(NAME_SHARED_PREFERENCE, MODE_PRIVATE)

    fun onSignButtonPressed() {
        val enteredToken = repository.keyValueStorage.authToken ?: ""
        if (tokenIsValid(enteredToken)) {
            _state.value = State.Loading

            viewModelScope.launch {
                // try{Action.RouteToMain}
                try {
                    val user = repository.signIn(enteredToken)
                    token.value = enteredToken
                    saveTokenInSharedPref(enteredToken)
                    Log.d("TEST_TOKEN", user.name)

//                    val listRepos = repository.getRepositories()
//                    for ((i, repo) in listRepos.withIndex()) {
//                        Log.d("TEST_TOKEN", "repo${i + 1}: $repo")
//                    }

                    delay(1000)
                    _state.value = State.Idle
                } catch (e: Exception) {
                    // catch{Action.ShowError(e.message)}
                    Log.d("TEST_TOKEN", "Exception onSignButtonPressed(): ${e.message}")
                }
            }
        }
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
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
        //token.value = newToken
        repository.keyValueStorage.authToken = newToken
    }

    private fun saveTokenInSharedPref(newToken: String?) {
        sharedPreferences.edit().putString(KEY_SHARED_PREFERENCE, newToken).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_SHARED_PREFERENCE, "")
    }

    companion object {
        const val NAME_SHARED_PREFERENCE = "shared_preference"
        const val KEY_SHARED_PREFERENCE = "token_value"
    }
}