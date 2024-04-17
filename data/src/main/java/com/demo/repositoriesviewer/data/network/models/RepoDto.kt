package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.SerializedName

data class RepoDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("forks_count")
    val forksCount: Int,

    @SerializedName("stargazers_count")
    val stargazersCount: Int,

    @SerializedName("default_branch")
    val branchName: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("language")
    val language: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("html_url")
    val url: String,

    @SerializedName("license")
    val license: LicenseDto?,

    @SerializedName("owner")
    val owner: OwnerDto
)