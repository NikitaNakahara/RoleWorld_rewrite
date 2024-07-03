package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse

class GetUserTask(private val id: String) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    override fun task() {
        val response = App.networkApi.getUser(id).execute()
        callback(response.body()!!)
    }
}