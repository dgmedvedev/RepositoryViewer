package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.usecases.GetRepositoriesUseCase
import kotlinx.coroutines.launch

class RepositoriesListViewModel : ViewModel() {

    val repository: AppRepositoryImpl = AppRepositoryImpl

    private val getRepositoriesUseCase = GetRepositoriesUseCase(repository)

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun loadData() {
        viewModelScope.launch {
            val repoList: List<Repo>
            try {
                _state.value = State.Loading
                repoList = getRepositoriesUseCase()
                _state.value = State.Loaded(repoList)
                if (repoList.isEmpty()) {
                    _state.value = State.Empty
                }
            } catch (error: Throwable) {
                showError(error)
            }
        }
    }

    private fun showError(error: Throwable) {
        when (error) {
            is Exception -> _state.value = State.Error(error.message.toString())
            is Error -> throw Error(error.message)
        }
    }

    sealed interface State {
        object Loading : State
        data class Loaded(val repos: List<Repo>) : State
        data class Error(val error: String) : State
        object Empty : State
    }
}