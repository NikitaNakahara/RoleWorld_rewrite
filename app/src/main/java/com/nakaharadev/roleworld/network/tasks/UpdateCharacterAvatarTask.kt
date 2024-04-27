package com.nakaharadev.roleworld.network.tasks

import android.graphics.Bitmap
import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.GlobalVariablesContainer
import com.nakaharadev.roleworld.UserData
import com.nakaharadev.roleworld.models.Character
import com.nakaharadev.roleworld.network.model.AbstractResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UpdateCharacterAvatarTask(private val avatar: Bitmap, private val character: Character) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    override fun task() {
        val cacheDir = GlobalVariablesContainer.get("cacheDir") as File? ?: return

        val cache = File("${cacheDir.path}/avatar")
        cache.createNewFile()

        val bos = ByteArrayOutputStream()
        avatar.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val data = bos.toByteArray()

        val fos = FileOutputStream(cache)
        fos.write(data)
        fos.flush()
        fos.close()

        val requestFile = cache.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("avatar", cache.name, requestFile)

        val response = App.networkApi.updateCharacterAvatar(character.id, body).execute()
        callback(response.body()!!)
    }
}