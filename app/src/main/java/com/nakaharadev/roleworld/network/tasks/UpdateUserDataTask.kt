package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.UserData
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.requests.UpdateRequest
import com.nakaharadev.roleworld.network.model.responses.UpdateResponse

class UpdateUserDataTask(val id: String, val value: String, val type: Int) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    companion object {
        val UPDATE_TYPE_NICKNAME = 1
    }

    @Throws(Exception::class)
    override fun task() {
        val response: UpdateResponse

        if (type == UPDATE_TYPE_NICKNAME)
            response = App.networkApi.updateNickname(UserData.id, UpdateRequest(value)).execute().body()!!
        else return

        callback(response)
    }

}