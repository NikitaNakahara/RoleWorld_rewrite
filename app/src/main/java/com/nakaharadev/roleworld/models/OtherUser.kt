package com.nakaharadev.roleworld.models

import android.graphics.Bitmap

data class OtherUser(
    val id: String,
    val showId: String,
    val nickname: String,
    val avatar: Bitmap?,
    val characters: String
) {
    data class SerializableOtherUser(
        val id: String,
        val showId: String,
        val nickname: String,
        val characters: String
    )

    fun toSerializable(): SerializableOtherUser {
        return SerializableOtherUser(id, showId, nickname, characters)
    }
}