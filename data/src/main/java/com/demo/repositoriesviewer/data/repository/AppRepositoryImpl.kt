package com.demo.repositoriesviewer.data.repository

import android.content.Context
import android.util.Log
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiService
import com.demo.repositoriesviewer.data.storage.KeyValueStorage
//import com.demo.repositoriesviewer.domain.models.Repo
//import com.demo.repositoriesviewer.domain.models.RepoDetails
import com.demo.repositoriesviewer.domain.models.RepoDetailsDomain
import com.demo.repositoriesviewer.domain.models.RepoItem
import com.demo.repositoriesviewer.domain.models.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AppRepositoryImpl(
    context: Context,
    private val apiService: ApiService
) :
    AppRepository {

    private val keyValueStorage = KeyValueStorage(context = context)
    private val mapper = RepoMapper
    private var userName: String? = null
    private var listRepoItems: List<RepoItem> = mutableListOf()

    override fun getToken(): String? {
        return keyValueStorage.authToken
    }

    override fun saveToken(newToken: String) {
        keyValueStorage.authToken = newToken
    }

//    override suspend fun getRepositories(): List<Repo> {
//        val fullListReposDto = apiService.getListRepos(userName = userName)
//        return mapper.mapListReposDtoToListRepos(listReposDto = fullListReposDto)
//    }

    override suspend fun getRepositoriesNew(): List<RepoItem> {
        val fullListRepoItemsDto = apiService.getListRepoItems(userName = userName)
        listRepoItems =
            mapper.mapListRepoItemsFromDtoToDomain(listRepoItemsDto = fullListRepoItemsDto)
        return listRepoItems
    }

//    override suspend fun getRepository(repoId: String): RepoDetails {
//        val listRepos = getRepositories()
//        var repoDetails: RepoDetails? = null
//        for (repo in listRepos) {
//            if (repo.id == repoId) {
//                setWatchers(repo = repo)
//                repoDetails = repo.repoDetails
//            }
//        }
//        return repoDetails ?: throw RuntimeException("Repository id = $repoId not found")
//    }

    override suspend fun getRepository(repoId: String): RepoDetailsDomain {
        var repoDetails: RepoDetailsDomain? = null
        for (repo in listRepoItems) {
            if (repo.id == repoId) {
                val watchers = getWatchers(repo)
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

//    override suspend fun signIn(token: String): UserInfo {
//        val authToken = " token $token"
//        val ownerDto = apiService.getOwnerDto(authToken = authToken)
//        userName = ownerDto.login
//        return mapper.ownerDtoToUserInfo(ownerDto = ownerDto)
//    }

    override suspend fun signIn(token: String): UserInfo {
//        val authToken = " token $token"
//        val ownerDto = apiService.getOwnerDto(authToken = authToken)
//        userName = ownerDto.login
        userName = "dgmedvedev"
        val listRepoItemDto = apiService.getListRepoItems(userName = userName)
        //Log.d("HOTFIX", "Result: $hotfix")
        Log.d("HOTFIX", "Result: $listRepoItemDto")
        return UserInfo("dgmedvedev")
    }

//    private suspend fun setWatchers(repo: Repo) {
//        val listWatchers =
//            apiService.getListWatchers(ownerName = userName, repositoryName = repo.repoDetails.name)
//        repo.repoDetails.watchers = listWatchers.size
//    }

    private suspend fun getWatchers(repo: RepoItem): Int {
        val listWatchers =
            apiService.getListWatchers(ownerName = userName, repositoryName = repo.name)
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