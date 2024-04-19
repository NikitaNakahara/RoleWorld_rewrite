package com.nakaharadev.roleworld.animators

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.nakaharadev.roleworld.Converter

object AuthAnimator {
    private val CHOOSER_HEIGHT = 50.0f
    private val SIGN_UP_HEIGHT = 230.0f
    private val SIGN_IN_HEIGHT = 130.0f

    fun chooserToAuth(
        chooser: LinearLayout,
        layout: RelativeLayout,
        signLayout: LinearLayout,
        isSignUp: Boolean = false
    ) {
        if (!isSignUp) chooserToSignIn(chooser, layout, signLayout)
        else chooserToSignUp(chooser, layout, signLayout)
    }

    fun signInToSignUp(
        layout: RelativeLayout,
        signIn: LinearLayout,
        signUp: LinearLayout,
    ) {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 300

        animator.addUpdateListener {
            signIn.alpha = it.animatedValue as Float
        }

        animator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                signIn.visibility = View.GONE

                val animator = ValueAnimator.ofFloat(SIGN_IN_HEIGHT, SIGN_UP_HEIGHT)

                animator.addUpdateListener {
                    val params = layout.layoutParams
                    params.height = Converter.dpToPx(it.animatedValue as Float).toInt()
                    layout.layoutParams = params
                }

                animator.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        signUp.visibility = View.VISIBLE

                        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)

                        animator.addUpdateListener {
                            signUp.alpha = it.animatedValue as Float
                        }

                        animator.start()
                    }

                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })


                animator.start()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

        })
        animator.start()
    }

    fun signUpToSignIn(
        layout: RelativeLayout,
        signIn: LinearLayout,
        signUp: LinearLayout,
    ) {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 300

        animator.addUpdateListener {
            signUp.alpha = it.animatedValue as Float
        }

        animator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                signUp.visibility = View.GONE

                val animator = ValueAnimator.ofFloat(SIGN_UP_HEIGHT, SIGN_IN_HEIGHT)

                animator.addUpdateListener {
                    val params = layout.layoutParams
                    params.height = Converter.dpToPx(it.animatedValue as Float).toInt()
                    layout.layoutParams = params
                }

                animator.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        signIn.visibility = View.VISIBLE

                        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)

                        animator.addUpdateListener {
                            signIn.alpha = it.animatedValue as Float
                        }

                        animator.start()
                    }

                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })


                animator.start()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

        })
        animator.start()
    }

    private fun chooserToSignIn(
        chooser: LinearLayout,
        layout: RelativeLayout,
        signInLayout: LinearLayout
    ) {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 300

        animator.addUpdateListener {
            chooser.alpha = it.animatedValue as Float
        }

        animator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                chooser.visibility = View.GONE

                val animator = ValueAnimator.ofFloat(CHOOSER_HEIGHT, SIGN_IN_HEIGHT)

                animator.addUpdateListener {
                    val params = layout.layoutParams
                    params.height = Converter.dpToPx(it.animatedValue as Float).toInt()
                    layout.layoutParams = params
                }

                animator.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        signInLayout.visibility = View.VISIBLE

                        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)

                        animator.addUpdateListener {
                            signInLayout.alpha = it.animatedValue as Float
                        }

                        animator.start()
                    }

                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })


                animator.start()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

        })
        animator.start()
    }

    private fun chooserToSignUp(
        chooser: LinearLayout,
        layout: RelativeLayout,
        signUpLayout: LinearLayout
    ) {
        val animator = ValueAnimator.ofFloat(1.0f, 0.0f)
        animator.duration = 300

        animator.addUpdateListener {
            chooser.alpha = it.animatedValue as Float
        }

        animator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                chooser.visibility = View.GONE

                val animator = ValueAnimator.ofFloat(CHOOSER_HEIGHT, SIGN_UP_HEIGHT)

                animator.addUpdateListener {
                    val params = layout.layoutParams
                    params.height = Converter.dpToPx(it.animatedValue as Float).toInt()
                    layout.layoutParams = params
                }

                animator.addListener(object: Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator) {
                        signUpLayout.visibility = View.VISIBLE

                        val animator = ValueAnimator.ofFloat(0.0f, 1.0f)

                        animator.addUpdateListener {
                            signUpLayout.alpha = it.animatedValue as Float
                        }

                        animator.start()
                    }

                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })

                animator.start()
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}

        })
        animator.start()
    }

    fun showErrorMessage(
        text: String,
        errorDrawable: Drawable,
        errorView: TextView,
        views: List<TextView>
    ) {
        for (view: TextView in views) {
            view.setBackgroundDrawable(errorDrawable)
        }

        errorView.text = text
        val animator = ObjectAnimator.ofFloat(errorView, "alpha", 0.0f, 1.0f)
        animator.duration = 300
        animator.start()
    }
}