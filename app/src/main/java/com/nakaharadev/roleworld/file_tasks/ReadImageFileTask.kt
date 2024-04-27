package com.nakaharadev.roleworld.file_tasks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.nakaharadev.roleworld.UserData
import java.io.File
import java.io.FileInputStream

class ReadImageFileTask(val name: String) : AbstractReadFileTask() {
    override lateinit var callback: (data: Any?) -> Unit

    override fun read(): String? {
        val file = File(name)
        if (!file.exists()) return null

        val stream = FileInputStream(file)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        callback(BitmapFactory.decodeStream(stream, null, options))

        return "filename=${name}"
    }
}