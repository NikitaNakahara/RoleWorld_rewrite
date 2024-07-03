package com.nakaharadev.roleworld.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nakaharadev.roleworld.models.Friend
import com.nakaharadev.roleworld.models.OtherUser

class FriendLayout
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0,
            defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var friend: Friend? = null

    fun setFriend(data: Friend) {
        friend = data
    }

    fun getFriend(): Friend? {
        return friend
    }
}