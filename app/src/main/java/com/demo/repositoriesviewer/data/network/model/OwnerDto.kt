package com.demo.repositoriesviewer.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OwnerDto(
    @SerializedName("login")
    @Expose
    val login: String
)