package com.demo.repositoriesviewer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryReadmeUseCase
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryUseCase
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

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

    suspend fun loadData(repoId: String) {
            val markdown: String
            val repo: Repo
            val repositoryDetails: RepoDetails
            try {
                _state.value = State.Loading
                _readmeState.value = ReadmeState.Loading

                repositoryDetails = getRepositoryUseCase(repoId)
                repo = Repo(repoId, repositoryDetails)
                val ownerName = repo.repoDetails.userInfo?.name
                val repositoryName = repo.repoDetails.name
                val branchName = repo.repoDetails.branchName

                if (!ownerName.isNullOrBlank()) {
                    try {
                        markdown = getRepositoryReadmeUseCase(
                            ownerName,
                            repositoryName,
                            branchName
                        )
                        val flavour = CommonMarkFlavourDescriptor()
                        val parsedTree =
                            MarkdownParser(flavour).buildMarkdownTreeFromString(markdown)
                        val html = HtmlGenerator(markdown, parsedTree, flavour).generateHtml()
                        _readmeState.value = ReadmeState.Loaded(html)
                    } catch (e: Exception) {
                        if (e.message == "Empty") {
                            _readmeState.value = ReadmeState.Empty
                        } else {
                            _readmeState.value = ReadmeState.Error(e.message.toString())
                        }
                    }
                    _state.value = State.Loaded(repo, ReadmeState.Loading)
                    if (ownerName.isEmpty() && repositoryName.isEmpty() && branchName.isEmpty()) {
                        _readmeState.value = ReadmeState.Empty
                    }
                } else {
                    _readmeState.value = ReadmeState.Error("ownerName is null or blank")
                }
            } catch (error: Throwable) {
                showError(error)
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