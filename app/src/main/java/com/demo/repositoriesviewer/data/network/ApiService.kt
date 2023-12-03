package com.demo.repositoriesviewer.data.network

import com.demo.repositoriesviewer.data.network.model.RepoDto
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {

    @GET("user/repos")
    @Headers("Authorization: Bearer $HEADER_PARAM_TOKEN")
    suspend fun getFullReposList(): List<RepoDto>

    companion object {
        const val HEADER_PARAM_TOKEN = "TODO()"
    }
}