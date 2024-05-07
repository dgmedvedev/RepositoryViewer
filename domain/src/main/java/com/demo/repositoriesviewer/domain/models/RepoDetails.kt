package com.demo.repositoriesviewer.domain.models

data class RepoDetails(
    val name: String,
    val url: String,
    val license: License?,
    val stars: Int,
    val forks: Int,
    val branchName: String,
    val userInfo: UserInfo?,
    val watchers: Int
)