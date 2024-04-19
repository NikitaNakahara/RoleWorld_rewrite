package com.nakaharadev.roleworldserver.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetCharacterResponse(
    @SerializedName("status")
    @Expose
    val status: Int,

    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("name")
    @Expose
    val name: String,
)