package com.demo.repositoriesviewer.data

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat.getString
import com.demo.repositoriesviewer.R
import com.demo.repositoriesviewer.data.mapper.RepoMapper
import com.demo.repositoriesviewer.data.network.ApiFactory
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo
import com.demo.repositoriesviewer.domain.repository.AppRepository

class AppRepositoryImpl(private val context: Context) : AppRepository {

    private val apiService = ApiFactory.apiService

    private val mapper = RepoMapper()
    private val token = ""

    override suspend fun getRepositories(): List<Repo> {
        val userName = signIn(token).name
        Log.d("TEST_RETROFIT", "Authorization OK, userName - $userName")
        val fullListReposDto = apiService.getFullListRepos(userName)
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
        return repoDetails ?: throw java.lang.Exception("Repository id = $repoId not found")
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(token: String): UserInfo {
        val authorizationHeader = String.format(
            getString(context, R.string.authorization_header),
            token
        )
        val ownerDto = apiService.getOwnerDto(authorizationHeader)
        return mapper.ownerDtoToUserInfo(ownerDto)
    }
}