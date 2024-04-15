package com.demo.repositoriesviewer.data.repository

import android.content.Context
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiService
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
import com.demo.repositoriesviewer.domain.models.Repo
import com.demo.repositoriesviewer.domain.models.RepoDetails
import com.demo.repositoriesviewer.domain.models.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AppRepositoryImpl(
    context: Context,
    private val apiService: ApiService,
    private val mapper: RepoMapper
) :
    AppRepository {

    private val keyValueStorage = KeyValueStorage(context = context)
    private lateinit var userName: String

    override fun getToken(): String? {
        return keyValueStorage.authToken
    }

    override fun saveToken(newToken: String) {
        keyValueStorage.authToken = newToken
    }

    override suspend fun getRepositories(): List<Repo> {
        val fullListReposDto = apiService.getListRepos(userName = userName)
        return mapper.mapListReposDtoToListRepos(listReposDto = fullListReposDto)
    }

    override suspend fun getRepository(repoId: String): RepoDetails {
        val listRepos = getRepositories()
        var repoDetails: RepoDetails? = null
        for (repo in listRepos) {
            if (repo.id == repoId) {
                setWatchers(repo = repo)
                repoDetails = repo.repoDetails
            }
        }
        return repoDetails ?: throw RuntimeException("Repository id = $repoId not found")
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        val downloadUrl: String
        try {
            downloadUrl = withContext(Dispatchers.IO) {
                val jsonObject = apiService.getReadme(
                    ownerName = ownerName,
                    repositoryName = repositoryName,
                    branchName = branchName
                )
                jsonObject.get("download_url").asString
            }
        } catch (e: Exception) {
            throw Exception("Empty")
        }
        return downloadRawReadme(downloadUrl = downloadUrl)
    }

    override suspend fun signIn(token: String): UserInfo {
        val authorizationHeader = " token $token"
        val ownerDto = apiService.getOwnerDto(authorization = authorizationHeader)
        userName = ownerDto.login
        return mapper.ownerDtoToUserInfo(ownerDto = ownerDto)
    }

    private suspend fun setWatchers(repo: Repo) {
        val listWatchers =
            apiService.getListWatchers(ownerName = userName, repositoryName = repo.repoDetails.name)
        repo.repoDetails.watchers = listWatchers.size
    }

    private suspend fun downloadRawReadme(downloadUrl: String): String =
        withContext(Dispatchers.IO) {
            var urlConnection: HttpURLConnection? = null
            var rawReadme: String

            try {
                val url = URL(downloadUrl)
                urlConnection = url.openConnection() as HttpURLConnection
                with(urlConnection) {
                    rawReadme = inputStream.bufferedReader().use { it.readText() }
                }
            } catch (e: Exception) {
                throw Exception("Exception in the AppRepositoryImpl: ${e.message}")
            } finally {
                urlConnection?.disconnect()
            }
            rawReadme
        }
}