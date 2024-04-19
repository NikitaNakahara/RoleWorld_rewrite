package com.nakaharadev.roleworld.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0,

    @SerializedName("userId")
    @Expose
    val userId: String,

    @SerializedName("nickname")
    @Expose
    val nickname: String
)
