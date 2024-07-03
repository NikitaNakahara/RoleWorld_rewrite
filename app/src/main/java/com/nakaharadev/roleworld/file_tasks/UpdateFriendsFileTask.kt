package com.nakaharadev.roleworld.file_tasks

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.nakaharadev.roleworld.models.Friend
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UpdateFriendsFileTask(val filename: String, val friend: Friend, val mode: Int) : AbstractFileTask() {
    companion object {
        val UPDATE_MODE_ADD = 1
        val UPDATE_MODE_CHANGE = 2
    }

    override fun save(): String? {
        val file = File(filename)
        if (mode == UpdateCharactersFileTask.UPDATE_MODE_ADD) {
            return addToFile(file)
        } else if (mode == UpdateCharactersFileTask.UPDATE_MODE_CHANGE) {
            return changeCharacter(file)
        }

        return null
    }

    private fun addToFile(file: File): String {
        if (!file.exists())
            return createAndAdd(file)
        else
            return addAndModify(file)
    }

    private fun createAndAdd(file: File): String {
        file.createNewFile()

        val array = JsonArray()

        val gson = GsonBuilder().create()
        array.add(gson.toJsonTree(friend.toSerializable()))

        val json = gson.toJson(array)

        val stream = DataOutputStream(FileOutputStream(file))
        stream.writeUTF(json)
        stream.flush()
        stream.close()

        return json
    }

    private fun addAndModify(file: File): String {
        val stream = DataInputStream(FileInputStream(file))
        var json = stream.readUTF()
        stream.close()

        val gson = GsonBuilder().create()
        val array = gson.fromJson(json, JsonArray::class.java)
        array.add(gson.toJsonTree(friend.toSerializable()))

        json = gson.toJson(array)

        val outputStream = DataOutputStream(FileOutputStream(file))
        outputStream.writeUTF(json)
        outputStream.flush()
        outputStream.close()

        return json
    }

    private fun changeCharacter(file: File): String {
        val stream = DataInputStream(FileInputStream(file))
        var json = stream.readUTF()
        stream.close()

        val gson = GsonBuilder().create()
        val array = gson.fromJson(json, JsonArray::class.java)

        val newArray = JsonArray()

        for (elem: JsonElement in array) {
            val obj = elem.asJsonObject

            if (obj.get("id").asJsonObject.get("id").asString == friend.user.id) {
                newArray.add(gson.toJsonTree(friend.toSerializable()))
            } else {
                newArray.add(obj)
            }
        }

        json = gson.toJson(newArray)

        val outputStream = DataOutputStream(FileOutputStream(file))
        outputStream.writeUTF(json)
        outputStream.flush()
        outputStream.close()

        return json
    }
}