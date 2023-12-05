package com.demo.repositoriesviewer.data.network

import com.demo.repositoriesviewer.data.network.model.OwnerDto
import com.demo.repositoriesviewer.data.network.model.RepoDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("user/repos")
    suspend fun getListRepos(
        @Header(HEADER_PARAM_AUTHORIZATION) authorization: String?,
        @Query(QUERY_PARAM_AFFILIATION) affiliation: String = "owner",
        @Query(QUERY_PARAM_PER_PAGE) perPage: Int = 10
    ): List<RepoDto>

    @GET("users/{user}/repos")
    suspend fun getFullListRepos(
        @Path("user") user: String?,
        @Query(QUERY_PARAM_PER_PAGE) perPage: Int = 10
    ): List<RepoDto>

    @GET("user")
    suspend fun getOwnerDto(
        @Header(HEADER_PARAM_AUTHORIZATION) authorization: String?
    ): OwnerDto

    companion object {
        private const val HEADER_PARAM_AUTHORIZATION = "Authorization"
        private const val QUERY_PARAM_AFFILIATION = "affiliation"
        private const val QUERY_PARAM_PER_PAGE = "per_page"
    }
}