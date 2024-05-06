package com.demo.repositoriesviewer.data.network

import com.demo.repositoriesviewer.data.network.models.OwnerDto
import com.demo.repositoriesviewer.data.network.models.RepoDetailsDto
import com.demo.repositoriesviewer.data.network.models.RepoDto
import com.demo.repositoriesviewer.data.network.models.RepoItemDto
import com.demo.repositoriesviewer.data.network.models.WatcherDto
import com.google.gson.JsonObject
import retrofit2.http.*

interface ApiService {

//    @GET("users/{username}/repos")
//    suspend fun getListRepos(
//        @Path(PATH_PARAM_USERNAME) userName: String?,
//        @Query(QUERY_PARAM_PER_PAGE) perPage: Int = 10
//    ): List<RepoDto>

    @GET("users/{username}/repos")
    suspend fun getListRepoItems(
        @Path(PATH_PARAM_USERNAME) userName: String?,
        @Query(QUERY_PARAM_PER_PAGE) perPage: Int = 10
    ): List<RepoItemDto>

    @GET("repos/{owner}/{repo}")
    suspend fun getRepoDetails(
        @Path(PATH_PARAM_OWNER) ownerName: String?,
        @Path(PATH_PARAM_REPO) repositoryName: String
    ): RepoDetailsDto

    @GET("repos/{owner}/{repo}/subscribers")
    suspend fun getListWatchers(
        @Path(PATH_PARAM_OWNER) ownerName: String?,
        @Path(PATH_PARAM_REPO) repositoryName: String
    ): List<WatcherDto>

    @GET("user")
    suspend fun getOwnerDto(
        @Header(HEADER_PARAM_AUTHORIZATION) authToken: String?
    ): OwnerDto

    @GET("repos/{owner}/{repo}/readme")
    suspend fun getReadme(
        @Path(PATH_PARAM_OWNER) ownerName: String,
        @Path(PATH_PARAM_REPO) repositoryName: String,
        @Query(QUERY_PARAM_REF) branchName: String
    ): JsonObject

    companion object {
        private const val HEADER_PARAM_AUTHORIZATION = "Authorization"
        private const val QUERY_PARAM_PER_PAGE = "per_page"
        private const val QUERY_PARAM_REF = "ref"
        private const val PATH_PARAM_USERNAME = "username"
        private const val PATH_PARAM_OWNER = "owner"
        private const val PATH_PARAM_REPO = "repo"
    }
}