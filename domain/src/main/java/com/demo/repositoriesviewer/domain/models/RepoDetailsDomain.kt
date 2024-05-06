package com.demo.repositoriesviewer.domain.models

data class RepoDetailsDomain(
    val repoName: String,
    val url: String,
    val license: License?,
    val stars: Int,
    val forks: Int,
    val branchName: String,
    val userInfo: UserInfo? = null,

    val watchers: Int
)