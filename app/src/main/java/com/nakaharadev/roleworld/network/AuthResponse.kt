package com.nakaharadev.roleworld.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0,

    @SerializedName("userId")
    @Expose
    val userId: String
)
