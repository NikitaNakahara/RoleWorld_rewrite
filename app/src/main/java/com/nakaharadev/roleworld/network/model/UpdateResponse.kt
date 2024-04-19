package com.nakaharadev.roleworld.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0
)
