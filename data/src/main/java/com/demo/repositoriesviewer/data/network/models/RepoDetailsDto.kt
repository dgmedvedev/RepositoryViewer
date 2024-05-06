package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.SerializedName

data class RepoDetailsDto(
    @SerializedName("name")
    val repoName: String,

    @SerializedName("html_url")
    val url: String,

    @SerializedName("license")
    val license: LicenseDto?,

    @SerializedName("stargazers_count")
    val stargazersCount: Int,

    @SerializedName("forks_count")
    val forksCount: Int,

    @SerializedName("default_branch")
    val branchName: String,

    @SerializedName("owner")
    val owner: OwnerDto
)