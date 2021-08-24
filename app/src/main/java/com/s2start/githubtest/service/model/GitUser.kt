package com.s2start.githubtest.service.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "User")
class GitUser {
    @PrimaryKey
    var id: Int = 0
    @SerializedName("login")
    var nickName: String = ""

    @SerializedName("avatar_url")
    var avatar: String = ""
    var favorite: Boolean = false
}

