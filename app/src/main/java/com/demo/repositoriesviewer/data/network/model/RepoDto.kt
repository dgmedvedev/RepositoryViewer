package com.demo.repositoriesviewer.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RepoDto(
    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("forks_count")
    @Expose
    val forksCount: Int,

    @SerializedName("stargazers_count")
    @Expose
    val stargazersCount: Int,

    @SerializedName("watchers_count")
    @Expose
    val watchersCount: Int,

    @SerializedName("default_branch")
    @Expose
    val branchName: String,

    @SerializedName("description")
    @Expose
    val description: String?,

    @SerializedName("language")
    @Expose
    val language: String,

    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("html_url")
    @Expose
    val url: String,

    @SerializedName("license")
    @Expose
    val license: LicenseDto?,

    @SerializedName("owner")
    @Expose
    val owner: OwnerDto
)