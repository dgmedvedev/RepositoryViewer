package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Watcher(
    @SerializedName("id")
    @Expose
    val id: Int
)