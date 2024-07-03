package com.nakaharadev.roleworld.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView

class AnimatedImageView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0,
            defStyleRes: Int = 0
) : ImageView(context, attrs, defStyleAttr, defStyleRes) {
    override fun setImageBitmap(bm: Bitmap?) {
        animateChange {
            super.setImageBitmap(bm)
        }
    }

    fun setImageBitmap(bm: Bitmap?, runAnimation: Boolean) {
        if (runAnimation) {
            setImageBitmap(bm)
        } else {
            super.setImageBitmap(bm)
        }
    }

    private fun animateChange(endCallback: () -> Unit) {
        val animator = ObjectAnimator.ofFloat(this@AnimatedImageView, "alpha", 1f, 0f)
        animator.duration = 200
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                endCallback()

                val endAnimator = ObjectAnimator.ofFloat(this@AnimatedImageView, "alpha", 0f, 1f)
                endAnimator.duration = 200
                endAnimator.start()
            }
        })
        animator.start()
    }
}