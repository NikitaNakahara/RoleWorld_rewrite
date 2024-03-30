package com.nakaharadev.roleworld.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.http.Query

interface NetworkApi {
    @POST("auth/sign_in")
    fun signIn(@Body body: AuthRequest.SignIn): Call<AuthResponse>

    @POST("auth/sign_up")
    fun signUp(@Body body: AuthRequest.SignUp): Call<AuthResponse>
}