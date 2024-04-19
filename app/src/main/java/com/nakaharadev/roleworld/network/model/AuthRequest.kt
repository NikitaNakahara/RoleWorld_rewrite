package com.nakaharadev.roleworld.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

object AuthRequest {
    data class SignIn(
        @SerializedName("email")
        @Expose
        val email: String,

        @SerializedName("password")
        @Expose
        val password: String
    )

    data class SignUp(
        @SerializedName("nickname")
        @Expose
        val nickname: String,

        @SerializedName("email")
        @Expose
        val email: String,

        @SerializedName("password")
        @Expose
        val password: String
    )
}