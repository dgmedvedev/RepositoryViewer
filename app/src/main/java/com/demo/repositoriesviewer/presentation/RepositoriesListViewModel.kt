package com.demo.repositoriesviewer.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.usecases.GetRepositoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    application: Application,
    private val getRepositoriesUseCase: GetRepositoriesUseCase
) : AndroidViewModel(application) {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = State.Loading
                val repoList = withContext(Dispatchers.IO) {
                    getRepositoriesUseCase()
                }
                _state.value = State.Loaded(repoList)
                if (repoList.isEmpty()) {
                    _state.value = State.Empty
                }
            } catch (error: Throwable) {
                showError(error)
            }
        }
    }

    suspend fun isInternetAvailable(): Boolean {
        return try {
            val ipAddress: InetAddress = withContext(Dispatchers.IO) {
                InetAddress.getByName(AVAILABLE_ADDRESS)
            }
            !ipAddress.equals(VALUE_IS_EMPTY)
        } catch (e: Exception) {
            false
        }
    }

    private fun showError(error: Throwable) {
        when (error) {
            is Exception -> _state.value = State.Error(error.message.toString())
            is Error -> throw Error(error.message)
        }
    }

    companion object {
        const val AVAILABLE_ADDRESS = "api.github.com"
        const val VALUE_IS_EMPTY = ""
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }
}