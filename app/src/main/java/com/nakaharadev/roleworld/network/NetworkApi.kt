package com.nakaharadev.roleworld.network

import com.nakaharadev.roleworld.network.model.requests.AuthRequest
import com.nakaharadev.roleworld.network.model.requests.UpdateRequest
import com.nakaharadev.roleworld.network.model.responses.AddResponse
import com.nakaharadev.roleworld.network.model.responses.AuthResponse
import com.nakaharadev.roleworld.network.model.responses.UpdateResponse
import com.nakaharadev.roleworld.network.model.responses.GetCharacterResponse
import com.nakaharadev.roleworld.network.model.responses.GetCharactersResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
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

    @Multipart
    @POST("update_user/{user_id}/add_character/{name}")
    fun addCharacter(@Path("user_id") id: String, @Path("name") characterName: String, @Part avatar: MultipartBody.Part): Call<AddResponse>

    @POST("update_user/{user_id}/nickname")
    fun updateNickname(@Path("user_id") id: String, @Body body: UpdateRequest): Call<UpdateResponse>

    @POST("update_character/{id}/{data_type}")
    fun updateCharacterData(@Path("id") id: String, @Path("data_type") dataType: String, @Body body: UpdateRequest): Call<UpdateResponse>

    @GET("get_user_data/{user_id}/avatar")
    fun getAvatar(@Path("user_id") id: String): Call<ResponseBody>

    @GET("get_user_data/{user_id}/characters")
    fun getCharacters(@Path("user_id") id: String): Call<GetCharactersResponse>

    @GET("get_character/{id}")
    fun getCharacter(@Path("id") id: String): Call<GetCharacterResponse>

    @GET("get_character/avatar/{id}")
    fun getCharacterAvatar(@Path("id") id: String): Call<ResponseBody>

    @Multipart
    @POST("update_character/{id}/avatar")
    fun updateCharacterAvatar(@Path("id") id: String, @Part file: MultipartBody.Part): Call<UpdateResponse>
}