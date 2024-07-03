package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse

class GetCharacterTask(private val id: String) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    override fun task() {
        val response = App.networkApi.getCharacter(id).execute().body()!!

        if (response.status == 200) {
            callback(response)
        }
    }
}