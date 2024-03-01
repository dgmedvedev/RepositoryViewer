package com.demo.repositoriesviewer.domain.repository

import com.demo.repositoriesviewer.domain.entities.KeyValue
import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo

interface AppRepository {

    suspend fun getRepositories(): List<Repo>

    suspend fun getRepository(repoId: String): RepoDetails

    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String

    suspend fun signIn(token: String): UserInfo

    fun getKeyValue(): KeyValue

    fun saveKeyValue(keyValue: KeyValue)
}