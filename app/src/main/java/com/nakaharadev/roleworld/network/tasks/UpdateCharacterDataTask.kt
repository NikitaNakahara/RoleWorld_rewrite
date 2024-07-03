package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.App
import com.nakaharadev.roleworld.network.model.AbstractRequest
import com.nakaharadev.roleworld.network.model.AbstractResponse
import com.nakaharadev.roleworld.network.model.requests.ValueRequest

class UpdateCharacterDataTask(val request: AbstractRequest, private val dataType: String, private val id: String) : AbstractTask() {
    override lateinit var callback: (AbstractResponse) -> Unit

    companion object {
        val DATA_TYPE_NAME = "name"
        val DATA_TYPE_SEX = "sex"
        val DATA_TYPE_DESC = "desc"
        val DATA_TYPE_BIO = "bio"
    }

    @Throws(Exception::class)
    override fun task() {
        val response = App.networkApi.updateCharacterData(id, dataType, request as ValueRequest).execute()
        callback(response.body()!!)
    }
}