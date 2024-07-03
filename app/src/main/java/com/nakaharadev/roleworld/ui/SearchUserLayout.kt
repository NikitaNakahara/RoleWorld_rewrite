package com.nakaharadev.roleworld.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nakaharadev.roleworld.models.OtherUser

class SearchUserLayout
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0,
            defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var userData: OtherUser? = null

    fun setUserData(data: OtherUser) {
        userData = data
    }

    fun getUserData(): OtherUser? {
        return userData
    }
}