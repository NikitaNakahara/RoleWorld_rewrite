package com.nakaharadev.roleworld.network.tasks

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.responses.GetAvatarResponse
import okhttp3.ResponseBody

class GetAvatarTask(val id: String, val avatarType: Int) : AbstractTask() {
    companion object {
        val AVATAR_TYPE_USER = 1
        val AVATAR_TYPE_CHARACTER = 2
    }

    override lateinit var callback: (AbstractResponse) -> Unit

    @Throws(Exception::class)
    override fun task() {
        val response: ResponseBody? = if (avatarType == AVATAR_TYPE_USER)
            App.networkApi.getAvatar(id).execute().body()
        else if (avatarType == AVATAR_TYPE_CHARACTER)
            App.networkApi.getCharacterAvatar(id).execute().body()
        else return

        val stream = response?.byteStream()
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val avatar = BitmapFactory.decodeStream(stream, null, options) ?: return

        callback(GetAvatarResponse(avatar))
    }
}