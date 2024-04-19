package com.nakaharadev.roleworld.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0,

    @SerializedName("id")
    @Expose
    val id: String
)