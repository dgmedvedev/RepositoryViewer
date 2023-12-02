package com.demo.repositoriesviewer.data.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LicenseDto(
    @SerializedName("spdx_id")
    @Expose
    val spdxId: String? = null
)