package com.nakaharadev.roleworld.network.model.responses

import android.graphics.Bitmap
import com.nakaharadev.roleworld.network.model.AbstractResponse

data class GetAvatarResponse(
    val avatar: Bitmap
) : AbstractResponse()