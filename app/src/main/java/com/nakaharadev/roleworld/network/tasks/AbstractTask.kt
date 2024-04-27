package com.nakaharadev.roleworld.network.tasks

import com.nakaharadev.roleworld.network.model.AbstractRequest
import com.nakaharadev.roleworld.network.model.AbstractResponse
import retrofit2.Call

abstract class AbstractTask() {
    abstract var callback: (AbstractResponse) -> Unit

    @Throws(Exception::class)
    abstract fun task()
}