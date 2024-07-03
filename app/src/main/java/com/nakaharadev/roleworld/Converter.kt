package com.nakaharadev.roleworld

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.TypedValue

@SuppressLint("StaticFieldLeak")
object Converter {
    private var context: Context? = null

    fun init(ctx: Context) {
        context = ctx
    }

    fun dpToPx(dp: Float): Float {
        if (context == null) {
            Log.e("${this.javaClass.simpleName}\$dpToPx(Float) -> Float", "Context is null")

            return 0f
        }

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context!!.resources.displayMetrics
        )
    }
}