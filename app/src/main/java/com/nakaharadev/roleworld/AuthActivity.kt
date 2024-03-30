package com.nakaharadev.roleworld

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.VideoView
import com.nakaharadev.roleworld.animators.AuthAnimator
import com.nakaharadev.roleworld.network.AuthRequest
import com.nakaharadev.roleworld.network.AuthResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.auth_layout)

        Converter.init(this)

        initAuthChooser()
        initSignIn()
        initSignUp()
    }

    override fun onStart() {
        super.onStart()

        initAuthBg()
    }

    private fun initAuthBg() {
        val view = findViewById<View>(R.id.auth_bg) as VideoView

        view.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = view.width / view.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                view.scaleX = scaleX
            } else {
                view.scaleY = 1f / scaleX
            }
        }

        val uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.auth_bg)
        view.setVideoURI(uri)
        view.setOnCompletionListener {
            view.start()
        }
        view.start()
    }

    private fun initAuthChooser() {
        findViewById<TextView>(R.id.sign_in).setOnClickListener {
            AuthAnimator.chooserToAuth(
                findViewById(R.id.auth_chooser),
                findViewById(R.id.auth_layout),
                findViewById(R.id.sign_in_layout)
            )
        }

        findViewById<TextView>(R.id.sign_up).setOnClickListener {
            AuthAnimator.chooserToAuth(
                findViewById(R.id.auth_chooser),
                findViewById(R.id.auth_layout),
                findViewById(R.id.sign_up_layout),
                true
            )
        }

        findViewById<TextView>(R.id.sign_in_sign_up).setOnClickListener {
            AuthAnimator.signInToSignUp(
                findViewById(R.id.auth_layout),
                findViewById(R.id.sign_in_layout),
                findViewById(R.id.sign_up_layout)
            )
        }

        findViewById<TextView>(R.id.sign_up_sign_in).setOnClickListener {
            AuthAnimator.signUpToSignIn(
                findViewById(R.id.auth_layout),
                findViewById(R.id.sign_in_layout),
                findViewById(R.id.sign_up_layout)
            )
        }
    }

    private fun initSignIn() {
        findViewById<EditText>(R.id.sign_in_password).setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val request = AuthRequest.SignIn(
                    findViewById<EditText>(R.id.sign_in_email).text.toString(),
                    findViewById<EditText>(R.id.sign_in_password).text.toString()
                )
                App.networkApi.signIn(request).enqueue(object: Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {

                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }
            return@setOnEditorActionListener true
        }
    }

    private fun initSignUp() {
        findViewById<EditText>(R.id.sign_up_repeat_password).setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val password = findViewById<TextView>(R.id.sign_up_password)
                val repeatPassword = findViewById<TextView>(R.id.sign_up_repeat_password)

                if (password.text.toString() != repeatPassword.text.toString()) {
                    password.text = ""
                    repeatPassword.text = ""

                    return@setOnEditorActionListener false
                }

                val request = AuthRequest.SignUp(
                    findViewById<EditText>(R.id.sign_up_nickname).text.toString(),
                    findViewById<EditText>(R.id.sign_up_email).text.toString(),
                    password.text.toString()
                )
                App.networkApi.signUp(request).enqueue(object: Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {

                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }
            return@setOnEditorActionListener true
        }
    }
}