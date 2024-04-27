package com.nakaharadev.roleworld.file_tasks

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class SaveTextFileTask(val name: String, val text: String) : AbstractFileTask() {
    override fun save(): String {
        val file = File(name)
        if (!file.exists()) file.createNewFile()

        val stream = DataOutputStream(FileOutputStream(file))
        stream.writeUTF(text)
        stream.flush()
        stream.close()

        return text
    }
}