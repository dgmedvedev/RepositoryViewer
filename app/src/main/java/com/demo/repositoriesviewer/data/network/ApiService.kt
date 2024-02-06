package com.demo.repositoriesviewer.data.network

import com.demo.repositoriesviewer.data.network.model.OwnerDto
import com.demo.repositoriesviewer.data.network.model.RepoDto
import com.demo.repositoriesviewer.data.network.model.Watcher
import com.google.gson.JsonObject
import retrofit2.http.*

interface ApiService {

    @GET("users/{username}/repos")
    suspend fun getListRepos(
        @Path(PATH_PARAM_USERNAME) userName: String?,
        @Query(QUERY_PARAM_PER_PAGE) perPage: Int = 10
    ): List<RepoDto>

    @GET("repos/{owner}/{repo}/subscribers")
    suspend fun getListWatchers(
        @Path(PATH_PARAM_OWNER) ownerName: String,
        @Path(PATH_PARAM_REPO) repositoryName: String
    ): List<Watcher>

    @GET("user")
    suspend fun getOwnerDto(
        @Header(HEADER_PARAM_AUTHORIZATION) authorization: String?
    ): OwnerDto

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path(PATH_PARAM_OWNER) ownerName: String,
        @Path(PATH_PARAM_REPO) repositoryName: String,
        @Query(QUERY_PARAM_REF) branchName: String
    ): JsonObject

    @POST("markdown")
    suspend fun convertReadme(@Body text: String): String

    companion object {
        private const val HEADER_PARAM_AUTHORIZATION = "Authorization"
        private const val QUERY_PARAM_PER_PAGE = "per_page"
        private const val QUERY_PARAM_REF = "ref"
        private const val PATH_PARAM_USERNAME = "username"
        private const val PATH_PARAM_OWNER = "owner"
        private const val PATH_PARAM_REPO = "repo"
    }
}