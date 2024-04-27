package com.nakaharadev.roleworld.network.model.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractResponse

data class UpdateResponse(
    @SerializedName("status")
    @Expose
    val status: Int = 0
) : AbstractResponse()