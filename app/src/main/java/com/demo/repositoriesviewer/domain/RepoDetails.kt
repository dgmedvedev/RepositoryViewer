package com.demo.repositoriesviewer.domain

data class RepoDetails(
    val forks: Int,
    val stars: Int,
    val watchers: Int,
    val url: String,
    val license: String,
    val readme: String
)