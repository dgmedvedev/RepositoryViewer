package com.demo.repositoriesviewer.domain.repository

import com.demo.repositoriesviewer.domain.models.Repo
import com.demo.repositoriesviewer.domain.models.RepoDetails
import com.demo.repositoriesviewer.domain.models.UserInfo

interface AppRepository {

    suspend fun getRepositories(): List<Repo>

    suspend fun getRepository(repoId: String): RepoDetails

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String

    suspend fun signIn(token: String): UserInfo

    fun clearToken()

    fun getToken(): String?

    fun saveToken(newToken: String)

    fun loggedIn(): Boolean
}