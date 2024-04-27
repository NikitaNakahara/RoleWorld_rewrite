package com.nakaharadev.roleworld

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        if (checkPreferences(preferences)) {
            fillUserData(preferences)
            startActivity(Intent(this, AppActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    private fun checkPreferences(preferences: SharedPreferences): Boolean {
        return preferences.getString("nickname", null) != null
    }

    private fun fillUserData(preferences: SharedPreferences) {
        UserData.id = preferences.getString("user_id", "").toString()
        UserData.nickname = preferences.getString("nickname", "").toString()
        UserData.email = preferences.getString("email", "").toString()
        UserData.password = preferences.getString("password", "").toString()
        UserData.showId = preferences.getString("show_id", "").toString()
    }
}