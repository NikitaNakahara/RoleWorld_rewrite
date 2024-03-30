package com.nakaharadev.roleworld

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager

class LauncherActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (checkPreferences(preferences)) {
            fillUserData(preferences)
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
    }

    private fun checkPreferences(preferences: SharedPreferences): Boolean {
        return preferences.getString("nickname", null) != null
    }

    private fun fillUserData(preferences: SharedPreferences) {

    }
}