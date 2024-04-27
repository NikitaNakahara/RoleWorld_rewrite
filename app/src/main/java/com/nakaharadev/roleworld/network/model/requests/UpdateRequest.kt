package com.nakaharadev.roleworld.network.model.requests

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.nakaharadev.roleworld.network.model.AbstractRequest

class UpdateRequest(
    @SerializedName("value")
    @Expose
    val value: String
) : AbstractRequest()