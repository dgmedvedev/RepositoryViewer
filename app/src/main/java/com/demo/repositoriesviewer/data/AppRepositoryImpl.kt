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

class AppRepositoryImpl(context: Context) : AppRepository {

    private val apiService = ApiFactory.apiService

    private val mapper = RepoMapper()
    private val user = "dgmedvedev"
    private val token = ""
    private val authorizationHeader = String.format(
        getString(context, R.string.authorization_header),
        token
    )

    override suspend fun getRepositories(): List<Repo> {
        val fullListReposDto = apiService.getFullListRepos(user)
        return mapper.mapListReposDtoToListRepos(fullListReposDto)

//        val listReposDto = apiService.getListRepos(authorizationHeader)
//        return mapper.mapListReposDtoToListRepos(listReposDto)
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
        TODO("Not yet implemented")
    }
}