package com.nakaharadev.roleworld.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Resp(
    @SerializedName("status")
    @Expose
    val status: String
)
