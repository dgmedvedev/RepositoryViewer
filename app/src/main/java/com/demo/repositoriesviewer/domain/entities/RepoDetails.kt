package com.demo.repositoriesviewer.domain.entities

data class RepoDetails(
    val forks: Int,
    val stars: Int,
    val branchName: String,
    val description: String?,
    val language: String?,
    val name: String,
    val url: String,
    val license: License? = null,
    val userInfo: UserInfo? = null,
    var watchers: Int = 0
)