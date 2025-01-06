package com.demo.repositoriesviewer.data.repository

import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
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

class AppRepositoryImpl(private val keyValueStorage: KeyValueStorage) : AppRepository {

    private val apiService: ApiService = ApiFactory.getInstanceApiService()
    private val mapper: RepoMapper = RepoMapper
    private var userName: String = ""
    private var listRepos: List<Repo> = mutableListOf()

    override fun clearToken() {
        keyValueStorage.clear()
    }

    override fun getToken(): String? {
        return keyValueStorage.authToken
    }

    override fun saveToken(newToken: String) {
        keyValueStorage.authToken = newToken
    }

    override suspend fun getRepositories(): List<Repo> {
        val token = keyValueStorage.authToken
        token?.let { userName = signIn(it).name }
        val fullListReposDto = apiService.getListRepos(userName = userName)
        listRepos =
            mapper.mapListReposDtoToDomain(listReposDto = fullListReposDto)
        return listRepos
    }

    override suspend fun getRepository(repoId: String): RepoDetails {
        var repoDetails: RepoDetails? = null
        for (repo in listRepos) {
            if (repo.id == repoId) {
                val watchers = getWatchers(repo = repo)
                val repoDetailsDto =
                    apiService.getRepoDetails(ownerName = userName, repositoryName = repo.name)
                repoDetails = mapper.mapRepoDetailsDtoToDomain(
                    repoDetailsDto = repoDetailsDto,
                    watchers = watchers
                )
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
        val authToken = " token $token"
        val ownerDto = apiService.getOwnerDto(authToken = authToken)
        userName = ownerDto.login
        return mapper.ownerDtoToDomain(ownerDto = ownerDto)
    }

    override fun loggedIn(): Boolean = keyValueStorage.authToken != null

    private suspend fun getWatchers(repo: Repo): Int {
        val listWatchers = apiService.getListWatchers(
            ownerName = userName,
            repositoryName = repo.name
        )
        return listWatchers.size
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