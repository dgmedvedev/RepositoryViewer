package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryUseCase

class RepositoryInfoViewModel : ViewModel() {

    val repository: AppRepositoryImpl = AppRepositoryImpl

    private val getRepositoryUseCase = GetRepositoryUseCase(repository)

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    sealed interface State {
        object Loading : State
        data class Error(val error: String) : State

        data class Loaded(
            val githubRepo: Repo,
            val readmeState: ReadmeState
        ) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: String) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}