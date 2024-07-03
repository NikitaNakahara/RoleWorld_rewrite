package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.UserData
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.requests.ValueRequest
import com.nakaharadev.roleworld.network.model.responses.UpdateResponse

class UpdateUserDataTask(val id: String, val value: String, val type: Int) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    companion object {
        val UPDATE_TYPE_NICKNAME = 1
        val UPDATE_TYPE_SHOW_ID = 2
    }

    @Throws(Exception::class)
    override fun task() {
        val response: UpdateResponse

        if (type == UPDATE_TYPE_NICKNAME)
            response = App.networkApi.updateNickname(UserData.id, ValueRequest(value)).execute().body()!!
        else if (type == UPDATE_TYPE_SHOW_ID) {
            response = App.networkApi.updateShowId(UserData.id, ValueRequest(value)).execute().body()!!
        } else return

        callback(response)
    }

}