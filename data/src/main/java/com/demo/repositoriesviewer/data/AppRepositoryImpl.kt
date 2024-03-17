package com.demo.repositoriesviewer.data

import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiService
import com.demo.repositoriesviewer.data.storage.TokenStorage
import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AppRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val apiService: ApiService,
    private val mapper: RepoMapper
) :
    AppRepository {

    lateinit var userName: String

    override fun getKeyValue(): KeyValue {
        return mapper.keyValueStorageToKeyValue(tokenStorage.get())
    }

    override fun saveKeyValue(keyValue: KeyValue) {
        val keyValueStorage = mapper.keyValueToKeyValueStorage(keyValue)
        tokenStorage.save(keyValueStorage = keyValueStorage)
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
        val keyValue = KeyValue()
        keyValue.authToken = token
        saveKeyValue(keyValue = keyValue)
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