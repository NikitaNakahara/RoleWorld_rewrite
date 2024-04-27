package com.nakaharadev.roleworld.network.model.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractRequest
import com.nakaharadev.roleworld.network.model.AbstractResponse

object AuthRequest{
    data class SignIn(
        @SerializedName("email")
        @Expose
        val email: String,

        @SerializedName("password")
        @Expose
        val password: String
    ) : AbstractRequest()

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
    ) : AbstractRequest()
}