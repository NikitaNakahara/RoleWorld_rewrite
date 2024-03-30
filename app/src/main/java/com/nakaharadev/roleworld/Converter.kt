package com.nakaharadev.roleworld

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue

@SuppressLint("StaticFieldLeak")
object Converter {
    private lateinit var context: Context

    fun init(ctx: Context) {
        context = ctx
    }

    fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }
}