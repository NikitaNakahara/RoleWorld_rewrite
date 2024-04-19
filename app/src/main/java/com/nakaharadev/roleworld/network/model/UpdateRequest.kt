package com.nakaharadev.roleworld.network.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UpdateRequest(
    @SerializedName("value")
    @Expose
    val value: String
)