package com.s2start.githubtest.service.repository

import com.s2start.githubtest.service.model.GitUser
import com.s2start.githubtest.service.repository.local.UserDAO
import com.s2start.githubtest.service.repository.remote.GitHubApi

class GitUserRepository(private val mGitHubApi : GitHubApi, private val mDataBase: UserDAO){

    suspend fun getGitUserList(query: String) = mGitHubApi.getUserList(query)
    suspend fun getUserDetail(login: String) = mGitHubApi.getUserDetail(login)

    fun save(user: GitUser) = mDataBase.insert(user) > 0
    fun delete(user: GitUser) =  mDataBase.delete(user) > 0
    fun getAll() =  mDataBase.getAll()
    fun getFavoriteList() = mDataBase.getFavoriteList()
}