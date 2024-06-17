package com.demo.repositoriesviewer.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory private constructor() {

    companion object {

        private const val BASE_URL = "https://api.github.com/"

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        @Volatile
        private var instanceApiService: ApiService? = null

        fun getInstanceApiService(): ApiService {
            instanceApiService?.let { return it }
            synchronized(ApiFactory::class.java) {
                instanceApiService?.let { return it }
                val apiService: ApiService = retrofit.create(ApiService::class.java)
                instanceApiService = apiService
                return apiService
            }
        }
    }
}