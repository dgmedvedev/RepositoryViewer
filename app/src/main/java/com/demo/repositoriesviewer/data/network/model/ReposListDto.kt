package com.demo.repositoriesviewer.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReposListDto(
    @SerializedName("repos")
    @Expose
    val repos: List<RepoDto>? = null
)