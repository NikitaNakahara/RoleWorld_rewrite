package com.nakaharadev.roleworld.network.model.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractRequest

data class AddToFriendsRequest(
    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("friendId")
    @Expose
    val friendId: String
) : AbstractRequest()