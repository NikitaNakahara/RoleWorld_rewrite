package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.requests.AddToFriendsRequest

class AddToFriendsTask(val id: String, val friendId: String) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    override fun task() {
        val response = App.networkApi.addToFriends(AddToFriendsRequest(id, friendId)).execute()
        callback(response.body()!!)
    }
}