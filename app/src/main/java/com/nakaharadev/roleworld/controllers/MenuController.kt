package com.nakaharadev.roleworld.controllers

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.IdRes
import com.nakaharadev.roleworld.Converter

@SuppressLint("StaticFieldLeak")
object MenuController {
    val MENU_STATE_OPEN = 0
    val MENU_STATE_CLOSE = 1

    private var menuState = MENU_STATE_CLOSE;
    private var stateCallback: MenuStateCallback? = null
    private var menuButton: ImageView? = null
    private var menuLayout: LinearLayout? = null
    private var menuButtonsLayout: LinearLayout? = null
    private var buttonsCallbacks = HashMap<Int, ButtonCallback>()

    private var minAnimValue = 0f
    private var maxAnimValue = 0f
    private var animDuration = 0L

    fun setCallback(callback: MenuStateCallback) {
        stateCallback = callback
    }

    fun setMinAnimValue(value: Float) {
        minAnimValue = Converter.dpToPx(value)
    }

    fun setMaxAnimValue(value: Float) {
        maxAnimValue = Converter.dpToPx(value)
    }

    fun setAnimDuration(value: Long) {
        animDuration = value
    }

    fun setMenuLayout(layout: LinearLayout) {
        menuLayout = layout

        menuButtonsLayout = (layout.getChildAt(0) as ScrollView)
            .getChildAt(0) as LinearLayout
    }

    fun getMaxValue(): Float {
        return maxAnimValue
    }

    fun getMinValue(): Float {
        return minAnimValue
    }

    fun addButtonCallback(@IdRes id: Int, callback: ButtonCallback) {
        buttonsCallbacks[id] = callback
    }

    fun addButtonCallback(@IdRes id: Int, callback: () -> Unit) {
        buttonsCallbacks[id] = object : ButtonCallback {
            override fun onClick() {
                callback()
            }

        }
    }

    fun init(menuIcon: ImageView) {
        menuButton = menuIcon

        menuButton?.setOnClickListener {
            animate()
        }

        for (i: Int in 0 until menuButtonsLayout?.childCount!!) {
            menuButtonsLayout?.getChildAt(i)?.setOnClickListener {
                buttonsCallbacks[it.id]?.onClick()
                animate()
            }
        }
    }

    private fun animate() {
        val viewsMap = stateCallback?.onBeforeChangeState(menuState)

        val animator = if (menuState == MENU_STATE_CLOSE) {
            ValueAnimator.ofFloat(minAnimValue, maxAnimValue)
        } else {
            ValueAnimator.ofFloat(maxAnimValue, minAnimValue)
        }

        animator.duration = animDuration
        animator.addUpdateListener {
            stateCallback?.onChangeAnimValue(it.animatedValue as Float, viewsMap)
        }
        animator.addListener(object: AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                menuState = if (menuState == MENU_STATE_CLOSE) MENU_STATE_OPEN else MENU_STATE_CLOSE

                stateCallback?.onAfterChangeState(menuState, viewsMap)
            }
        })

        animator.start()
        if (menuButton?.drawable is Animatable) {
            (menuButton?.drawable as AnimatedVectorDrawable).start()
        }
    }


    abstract class MenuStateCallback {
        open fun onBeforeChangeState(oldState: Int): HashMap<String, View>? { return null }
        open fun onChangeAnimValue(value: Float, viewsMap: HashMap<String, View>?) {}
        open fun onAfterChangeState(newState: Int, viewsMap: HashMap<String, View>?) {}
    }

    interface ButtonCallback {
        fun onClick()
    }
}