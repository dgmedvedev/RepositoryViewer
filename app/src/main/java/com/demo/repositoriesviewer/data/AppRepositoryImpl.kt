package com.demo.repositoriesviewer.data

import android.util.Log
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

object AppRepositoryImpl : AppRepository {

    val keyValueStorage = KeyValueStorage()

    lateinit var userName: String

    private val apiService = ApiFactory.apiService
    private val mapper = RepoMapper()

    override suspend fun getRepositories(): List<Repo> {
        val fullListReposDto = apiService.getListRepos(userName = userName)
        return mapper.mapListReposDtoToListRepos(fullListReposDto)
    }

    override suspend fun getRepository(repoId: String): RepoDetails {
        val listRepos = getRepositories()
        var repoDetails: RepoDetails? = null
        for (repo in listRepos) {
            if (repo.id == repoId) {
                setWatchers(repo)
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
        thread { downloadRawReadme() }

        val json = apiService.getReadme(ownerName, repositoryName, branchName)
        return json.toString()
    }

    override suspend fun signIn(token: String): UserInfo {
        val authorizationHeader = " token $token"
        val ownerDto = apiService.getOwnerDto(authorizationHeader)
        userName = ownerDto.login
        keyValueStorage.authToken = token
        return mapper.ownerDtoToUserInfo(ownerDto)
    }

    private suspend fun setWatchers(repo: Repo) {
        val listWatchers = apiService.getListWatchers(userName, repo.repoDetails.name)
        repo.repoDetails.watchers = listWatchers.size
    }

    private fun downloadRawReadme() {
        val url = URL("https://raw.githubusercontent.com/dgmedvedev/Mechanic/master/README.md")

        try {
            val urlConnection = url.openConnection() as HttpURLConnection
            val inputStream = urlConnection.inputStream
            val reader = InputStreamReader(inputStream)

            val bufferReader = BufferedReader(reader)
            val line = bufferReader.use { it.readText() }
            Log.d("TEST_APP", "line1: $line")
        } catch (e: Exception) {
            Log.d("TEST_APP", "Exception:  $e")
        }

//        II method
//        with(url.openConnection() as HttpURLConnection) {
//            //requestMethod = "GET"  // optional default is GET
//            Log.d("TEST_APP", "\nSent 'GET' request to URL : $url; Response Code : $responseCode")
//            val line = inputStream.bufferedReader().use { it.readText() }
//            Log.d("TEST_APP", "line2: $line")
//        }
    }
}