package com.nakaharadev.roleworld.file_tasks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.nakaharadev.roleworld.UserData
import com.nakaharadev.roleworld.models.Character
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class ReadCharactersFileTask(private val filesPath: String) : AbstractReadFileTask() {
    /**
     * call for all characters
     * @param data character data (type is Character)
     */
    override lateinit var callback: (data: Any?) -> Unit

    override fun read(): String? {
        val file = File("${filesPath}/characters.json")
        if (!file.exists()) return null

        val stream = DataInputStream(FileInputStream(file))
        val jsonString = stream.readUTF()

        val gson = GsonBuilder().create()
        val array = gson.fromJson(jsonString, JsonArray::class.java)

        for (elem: JsonElement in array) {
            val character = Character()
            val obj = elem.asJsonObject
            character.id = obj["id"].asString
            character.name = obj["name"].asString
            character.desc = obj["desc"].asString
            character.bio = obj["bio"].asString
            character.sex = obj["sex"].asString

            val readTask = ReadImageFileTask("${filesPath}/${character.name}_avatar.png")
            readTask.callback = {
                character.avatar = it as Bitmap

                callback(character)
            }
            readTask.read()
        }

        return jsonString
    }
}