package com.nakaharadev.roleworld.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NetworkApi {
    @GET("request")
    fun request(@Query("req") req: String): Call<Resp>
}