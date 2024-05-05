package com.demo.repositoriesviewer.domain.models

data class RepoItem(
    val id: String,
    val name: String,
    val language: String?,
    val description: String?
)