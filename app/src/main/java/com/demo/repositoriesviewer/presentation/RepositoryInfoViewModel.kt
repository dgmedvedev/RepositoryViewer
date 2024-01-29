package com.demo.repositoriesviewer.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryReadmeUseCase
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryUseCase
import kotlinx.coroutines.launch

class RepositoryInfoViewModel : ViewModel() {

    val repository: AppRepositoryImpl = AppRepositoryImpl

    private val getRepositoryUseCase = GetRepositoryUseCase(repository)
    private val getRepositoryReadmeUseCase = GetRepositoryReadmeUseCase(repository)

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    private val _readmeState = MutableLiveData<ReadmeState>()
    val readmeState: LiveData<ReadmeState>
        get() = _readmeState

    fun loadData(repoId: String) {
        viewModelScope.launch {
            val readme: String
            val repo: Repo
            val repositoryDetails: RepoDetails
            try {
                _state.value = State.Loading
                _readmeState.value = ReadmeState.Loading

                repositoryDetails = getRepositoryUseCase(repoId)
                repo = Repo(repoId, repositoryDetails)
                _state.value = State.Loaded(repo, ReadmeState.Loading)
                val ownerName = repo.repoDetails.userInfo?.name
                val repositoryName = repo.repoDetails.name
                val branchName = repo.repoDetails.branchName

                if (!ownerName.isNullOrBlank()) {
                    readme = getRepositoryReadmeUseCase(ownerName, repositoryName, branchName)
                    Log.d("TEST_TOKEN", readme)
                    _readmeState.value = ReadmeState.Loaded(readme)
                    if (ownerName.isEmpty() && repositoryName.isEmpty() && branchName.isEmpty()) {
                        _readmeState.value = ReadmeState.Empty
                    }
                } else {
                    _readmeState.value = ReadmeState.Error("ownerName.isNullOrBlank()")
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