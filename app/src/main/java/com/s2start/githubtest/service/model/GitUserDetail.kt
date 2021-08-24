package com.s2start.githubtest.service.model

import com.google.gson.annotations.SerializedName

class GitUserDetail {
    var id: Int = 0

    @SerializedName("name")
    var nickname: String = ""

    var login: String = ""

    @SerializedName("avatar_url")
    var avatar: String = ""

    var location: String = ""
    var bio: String = ""
    var email: String = ""


}