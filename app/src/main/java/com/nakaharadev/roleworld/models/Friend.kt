package com.nakaharadev.roleworld.models

data class Friend(
    val user: OtherUser,
    var requested: Boolean = false
) {
    data class SerializableFriend(
        val user: OtherUser.SerializableOtherUser,
        var requested: Boolean = false
    )
    fun toSerializable(): SerializableFriend {
        return SerializableFriend(
            user.toSerializable(),
            requested
        )
    }
}