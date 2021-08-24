package com.s2start.githubtest.service.model

import com.google.gson.annotations.SerializedName

class ListGitUser {
    @SerializedName("items")
    var users: MutableList<GitUser> = mutableListOf()
}