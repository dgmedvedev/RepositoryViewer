package com.demo.repositoriesviewer.data

import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository

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
}