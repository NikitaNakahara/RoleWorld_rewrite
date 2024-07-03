package com.nakaharadev.roleworldserver.models

import com.nakaharadev.roleworld.network.model.AbstractResponse

data class AddToFriendsResponse(
    val status: Int
) : AbstractResponse()