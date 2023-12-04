package com.demo.repositoriesviewer.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RepoDto(
    @SerializedName("id")
    @Expose
    private val id: Int? = null,

    @SerializedName("name")
    @Expose
    private val name: String? = null,

    @SerializedName("owner")
    @Expose
    private val owner: OwnerDto? = null,

    @SerializedName("html_url")
    @Expose
    private val htmlUrl: String? = null,

    @SerializedName("description")
    @Expose
    private val description: String? = null,

    @SerializedName("stargazers_count")
    @Expose
    private val stargazersCount: Int? = null,

    @SerializedName("watchers_count")
    @Expose
    private val watchersCount: Int? = null,

    @SerializedName("forks_count")
    @Expose
    private val forksCount: Int? = null,

    @SerializedName("license")
    @Expose
    private val license: LicenseDto? = null,

    @SerializedName("default_branch")
    @Expose
    private val defaultBranch: String? = null
)