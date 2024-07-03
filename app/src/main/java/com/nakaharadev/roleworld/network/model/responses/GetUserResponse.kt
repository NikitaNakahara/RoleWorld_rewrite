package com.nakaharadev.roleworld.network.model.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractResponse

data class GetUserResponse(
    @SerializedName("showId")
    @Expose
    var showId: String,

    @SerializedName("nickname")
    @Expose
    var nickname: String,

    @SerializedName("avatar")
    @Expose
    val avatar: String,

    @SerializedName("characters")
    @Expose
    val characters: String
) : AbstractResponse()