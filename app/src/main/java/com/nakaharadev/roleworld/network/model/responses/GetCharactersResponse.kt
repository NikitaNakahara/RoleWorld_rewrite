package com.nakaharadev.roleworld.network.model.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractResponse

data class GetCharactersResponse(
    @SerializedName("characters")
    @Expose
    val characters: String
) : AbstractResponse()