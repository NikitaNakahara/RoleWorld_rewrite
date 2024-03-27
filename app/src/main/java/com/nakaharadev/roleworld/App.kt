package com.nakaharadev.roleworld

import android.app.Application
import com.nakaharadev.roleworld.network.NetworkApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {
    companion object {
        lateinit var networkApi: NetworkApi
    }

    override fun onCreate() {
        super.onCreate()

        configureRetrofit()
    }

    private fun configureRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.1.33:8080/app/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        networkApi = retrofit.create(NetworkApi::class.java)
    }
}