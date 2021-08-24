package com.s2start.githubtest.service.repository.remote

import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.service.model.GitUserDetail
import com.s2start.githubtest.service.model.ListGitUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/users")
    suspend fun getUserList(@Query("q") q : String) : ListGitUser

    @GET("users/{login}")
    suspend fun getUserDetail(@Path("login") login : String) : GitUserDetail
}