package com.demo.repositoriesviewer.presentation

import androidx.core.text.HtmlCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.repositoriesviewer.data.AppRepositoryImpl
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryReadmeUseCase
import com.demo.repositoriesviewer.domain.usecases.GetRepositoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun loadData(repoId: String) {
        viewModelScope.launch {
            try {
                _state.value = State.Loading

                val repo = downloadRepo(repoId)
                val ownerName = repo.repoDetails.userInfo?.name
                val repositoryName = repo.repoDetails.name
                val branchName = repo.repoDetails.branchName
                _state.value = State.Loaded(repo, ReadmeState.Loading)

                if (!ownerName.isNullOrBlank()) {
                    _readmeState.value = ReadmeState.Loading
                    try {
                        val markdown = withContext(Dispatchers.IO) {
                            val rawReadme = getRepositoryReadmeUseCase(
                                ownerName,
                                repositoryName,
                                branchName
                            )
                            rawReadmeToHtml(rawReadme)
                        }
                        _readmeState.value = ReadmeState.Loaded(markdown)
                    } catch (e: Exception) {
                        _readmeState.value =
                            if (e.message == "Empty") ReadmeState.Empty
                            else ReadmeState.Error(e.message.toString())
                    }
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
    }

    private suspend fun downloadRepo(repoId: String) =
        withContext(Dispatchers.IO) {
            val repositoryDetails = getRepositoryUseCase(repoId)
            Repo(repoId, repositoryDetails)
        }

    private fun rawReadmeToHtml(rawReadme: String) = run {
        val flavour = CommonMarkFlavourDescriptor()
        val parsedTree =
            MarkdownParser(flavour).buildMarkdownTreeFromString(rawReadme)
        val html = HtmlGenerator(rawReadme, parsedTree, flavour).generateHtml()
        HtmlCompat.fromHtml(
            html,
            HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
        ).toString()
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
        data class Loaded(val githubRepo: Repo, val readmeState: ReadmeState) : State
    }

    sealed interface ReadmeState {
        object Loading : ReadmeState
        object Empty : ReadmeState
        data class Error(val error: String) : ReadmeState
        data class Loaded(val markdown: String) : ReadmeState
    }
}