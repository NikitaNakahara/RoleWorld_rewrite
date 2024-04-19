package com.nakaharadev.roleworld.controllers

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.TextView
import com.nakaharadev.roleworld.Converter

@SuppressLint("StaticFieldLeak")
object InnerNotificationsController {
    val DONE = 1
    val ERROR = 2

    private lateinit var notificationView: TextView
    private lateinit var context: Context
    private lateinit var colorsMap: HashMap<Int, Int>

    fun init(ctx: Context, view: TextView, colors: HashMap<Int, Int>) {
        context = ctx
        notificationView = view
        colorsMap = colors
    }

    fun showNotification(data: String, mode: Int) {
        val drawable = (notificationView.background as StateListDrawable).getStateDrawable(0) as GradientDrawable
        drawable.setColor(colorsMap[mode]!!)
        notificationView.text = data

        show()
    }

    private fun show() {
        val animator = ObjectAnimator.ofFloat(notificationView, "translationY", Converter.dpToPx(-30f), 0f)
        animator.duration = 300
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                hide()
            }
        })
        animator.start()
    }

    private fun hide() {
        val animator = ObjectAnimator.ofFloat(notificationView, "translationY", 0f, Converter.dpToPx(-30f))
        animator.duration = 300
        animator.startDelay = 2000
        animator.start()
    }
}