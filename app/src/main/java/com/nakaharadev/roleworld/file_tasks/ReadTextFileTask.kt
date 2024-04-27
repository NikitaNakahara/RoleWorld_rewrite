package com.nakaharadev.roleworld.file_tasks

import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class ReadTextFileTask(val name: String) : AbstractReadFileTask() {
    override lateinit var callback: (data: Any?) -> Unit

    override fun read(): String? {
        val file = File(name)
        if (!file.exists()) return null

        val stream = DataInputStream(FileInputStream(file))
        val str = stream.readUTF()
        stream.close()

        callback(str)

        return str
    }
}