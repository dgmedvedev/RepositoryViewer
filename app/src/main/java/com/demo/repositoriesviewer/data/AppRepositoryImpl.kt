package com.demo.repositoriesviewer.data

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository

class AppRepositoryImpl(private val context: Context) : AppRepository {

    val keyValueStorage = KeyValueStorage

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
                repoDetails = repo.repoDetails
            }
        }
        return repoDetails ?: throw RuntimeException(
            String.format(
                getString(context, R.string.id_not_found),
                repoId
            )
        )
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        return "$ownerName+\n+$repositoryName+\n+$branchName"
    }

    override suspend fun signIn(token: String): UserInfo {
        val authorizationHeader = String.format(
            getString(context, R.string.authorization_header),
            token
        )
        val ownerDto = apiService.getOwnerDto(authorizationHeader)
        userName = ownerDto.login
        keyValueStorage.authToken = token
        return mapper.ownerDtoToUserInfo(ownerDto)
    }
}