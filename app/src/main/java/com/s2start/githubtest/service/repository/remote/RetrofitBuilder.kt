package com.s2start.githubtest.service.repository.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitBuilder {

    companion object {
        private const val BASE_URL = "https://api.github.com/"
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val  gitHubApi: GitHubApi = getRetrofit().create(GitHubApi::class.java)


}