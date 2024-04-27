package com.nakaharadev.roleworld.models

import android.graphics.Bitmap

class Character {
    var id = ""
    var name = ""
    var sex = ""
    var desc = ""
    var bio = ""
    var avatar: Bitmap? = null

    fun toSerializable(): Serializable {
        return Serializable(id, name, sex, desc, bio)
    }

    override fun toString(): String {
        return "{\n" +
                "\t\"id\": \"$id\",\n" +
                "\t\"name\": \"$name\",\n" +
                "\t\"sex\": \"$sex\",\n" +
                "\t\"desc\": \"$desc\",\n" +
                "\t\"bio\": \"$bio\",\n" +
                "}"
    }

    companion object {
        data class Serializable(
            val id: String,
            val name: String,
            val sex: String,
            val desc: String,
            val bio: String
        )
    }
}
