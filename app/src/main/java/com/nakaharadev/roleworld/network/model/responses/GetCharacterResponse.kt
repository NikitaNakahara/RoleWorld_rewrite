package com.nakaharadev.roleworld.network.model.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractResponse

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

    @SerializedName("sex")
    @Expose
    val sex: String,

    @SerializedName("desc")
    @Expose
    val desc: String,

    @SerializedName("bio")
    @Expose
    val bio: String
) : AbstractResponse()