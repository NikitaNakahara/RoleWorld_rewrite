package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.requests.ValueRequest

class SearchTask(private val value: ValueRequest) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    override fun task() {
        val response = App.networkApi.search(value).execute()
        callback(response.body()!!)
    }
}