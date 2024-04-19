package com.nakaharadev.roleworld.models

import android.graphics.Bitmap

class Character {
    var id = ""
    var name = ""
    var avatar: Bitmap? = null

    fun toSerializable(): Serializable {
        return Serializable(id, name)
    }

    override fun toString(): String {
        return "{\n" +
                "\t\"id\": \"$id\",\n" +
                "\t\"name\": \"$name\",\n" +
                "}"
    }

    companion object {
        data class Serializable(
            val id: String,
            val name: String
        )
    }
}
