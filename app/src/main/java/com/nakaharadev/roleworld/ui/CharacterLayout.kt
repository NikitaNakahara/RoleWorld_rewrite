package com.nakaharadev.roleworld.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class CharacterLayout
    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0
    ) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var characterId = ""

    fun setCharacterId(id: String) {
        characterId = id
    }

    fun getCharacterId(): String {
        return characterId
    }
}