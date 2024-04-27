package com.nakaharadev.roleworld.network.model.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractResponse

data class AuthResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0,

    @SerializedName("userId")
    @Expose
    val userId: String,

    @SerializedName("showId")
    @Expose
    val showId: String,

    @SerializedName("nickname")
    @Expose
    val nickname: String,

    @SerializedName("characters")
    @Expose
    val characters: String
) : AbstractResponse()
