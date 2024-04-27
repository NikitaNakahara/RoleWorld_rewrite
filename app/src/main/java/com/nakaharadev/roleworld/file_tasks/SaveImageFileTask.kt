package com.nakaharadev.roleworld.file_tasks

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SaveImageFileTask(val name: String, val bitmap: Bitmap) : AbstractFileTask() {
    override fun save(): String? {
        val file = File(name)
        if (!file.exists()) file.createNewFile()

        val fos = FileOutputStream(file)
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val data = bos.toByteArray()
        bos.close()

        fos.write(data)
        fos.flush()
        fos.close()

        return "filename=${name}"
    }
}