package com.nakaharadev.roleworld

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.nakaharadev.roleworld.network.Resp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.app_layout)

        val edit: EditText = findViewById(R.id.request)
        val text: TextView = findViewById(R.id.response)
        val send: TextView = findViewById(R.id.send)

        send.setOnClickListener {
            val request = edit.text.toString()
            App.networkApi.request(request).enqueue(object : Callback<Resp?> {
                override fun onResponse(call: Call<Resp?>, response: Response<Resp?>) {
                    runOnUiThread {
                        var status = response.body()?.status
                        if (status == null) status = "null"
                        text.text = status
                    }
                }

                override fun onFailure(call: Call<Resp?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
}