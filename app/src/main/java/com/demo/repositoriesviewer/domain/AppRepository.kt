package com.demo.repositoriesviewer.domain

interface AppRepository {

    fun getRepositories(): List<Repo>

    fun getRepository(repoId: String): RepoDetails

    fun getRepositoryReadme(ownerName: String, repositoryName: String, branchName: String): String

    fun signIn(token: String): UserInfo
}