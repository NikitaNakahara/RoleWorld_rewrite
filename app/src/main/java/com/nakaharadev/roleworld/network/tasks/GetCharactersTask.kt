package com.nakaharadev.roleworld.network.tasks

import android.util.Log
import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse

class GetCharactersTask(val id: String) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    @Throws(Exception::class)
    override fun task() {
        val request = App.networkApi.getCharacters(id).execute()

        if (!request.isSuccessful) {
            Log.e("network", "an error in ${this::class.simpleName}: code=${request.code()}")
            return
        }

        callback(request.body()!!)
    }

}