package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.domain.models.Repo
import com.demo.repositoriesviewer.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RepositoriesListViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = State.Loading
                val repoList = withContext(Dispatchers.IO) {
                    appRepository.getRepositories()
                }
                _state.value = State.Loaded(repoList)
                if (repoList.isEmpty()) {
                    _state.value = State.Empty
                }
            } catch (error: Throwable) {
                showError(error = error)
            }
        }
    }

    private fun showError(error: Throwable) {
        when (error) {
            is Exception -> _state.value = State.Error(error = error.message.toString())
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