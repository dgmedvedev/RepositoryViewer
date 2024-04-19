package com.demo.repositoriesviewer.data.network.models

import com.google.gson.annotations.SerializedName

data class LicenseDto(
    @SerializedName("spdx_id")
    val spdxId: String?
)