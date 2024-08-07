package com.nakaharadev.roleworld

import android.graphics.Bitmap
import com.nakaharadev.roleworld.models.Character
import com.nakaharadev.roleworld.models.Friend


object UserData {
    var id = ""
    var nickname = ""
    var email = ""
    var password = ""
    var showId = ""
    var roundedAvatar: Bitmap? = null
    var characters = HashMap<String, Character>()
    var charactersId = ArrayList<String>()
    var friends = ArrayList<Friend>()
}