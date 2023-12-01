package com.demo.repositoriesviewer.domain.repository

import com.demo.repositoriesviewer.domain.entities.Repo
import com.demo.repositoriesviewer.domain.entities.RepoDetails
import com.demo.repositoriesviewer.domain.entities.UserInfo

interface AppRepository {

    fun getRepositories(): List<Repo>

    fun getRepository(repoId: String): RepoDetails

    fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String

    fun signIn(token: String): UserInfo
}