package com.nakaharadev.roleworld.network

import com.nakaharadev.roleworld.network.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface NetworkApi {
    @POST("auth/sign_in")
    fun signIn(@Body body: AuthRequest.SignIn): Call<AuthResponse>

    @POST("auth/sign_up")
    fun signUp(@Body body: AuthRequest.SignUp): Call<AuthResponse>

    @Multipart
    @POST("update_user/{user_id}/avatar")
    fun updateAvatar(@Path("user_id") id: String, @Part avatar: MultipartBody.Part): Call<UpdateResponse>

    @POST("update_user/{user_id}/nickname")
    fun updateNickname(@Path("user_id") id: String, @Body body: UpdateRequest): Call<UpdateResponse>
}