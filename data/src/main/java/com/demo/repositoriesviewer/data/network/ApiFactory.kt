package com.demo.repositoriesviewer.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiFactory private constructor() {

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    companion object {

        private const val BASE_URL = "https://api.github.com/"

        @Volatile
        private var instance: ApiFactory? = null

        fun getInstance(): ApiFactory {
            instance?.let { return it }
            synchronized(ApiFactory::class.java) {
                instance?.let { return it }
                val apiFactory = ApiFactory()
                instance = apiFactory
                return apiFactory
            }
        }
    }
}