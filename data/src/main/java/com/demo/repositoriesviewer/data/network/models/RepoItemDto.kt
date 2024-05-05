package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.SerializedName

data class RepoItemDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("language")
    val language: String?,

    @SerializedName("description")
    val description: String?
)