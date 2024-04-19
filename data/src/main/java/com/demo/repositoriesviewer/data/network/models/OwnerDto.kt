package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.SerializedName

data class OwnerDto(
    @SerializedName("login")
    val login: String
)