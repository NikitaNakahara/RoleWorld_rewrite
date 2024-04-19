package com.nakaharadev.roleworld

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import com.nakaharadev.roleworld.animators.AuthAnimator
import com.nakaharadev.roleworld.controllers.MenuController
import com.nakaharadev.roleworld.network.model.AuthRequest
import com.nakaharadev.roleworld.network.model.AuthResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream


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
        val view = findViewById<VideoView>(R.id.auth_bg)

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
                findViewById<LinearLayout>(R.id.auth_load_indicator).visibility = View.VISIBLE
                val loadBar = findViewById<ImageView>(R.id.auth_load_bar)
                if (loadBar.drawable is Animatable) {
                    (loadBar.drawable as AnimatedVectorDrawable).start()
                }

                val request = AuthRequest.SignIn(
                    findViewById<EditText>(R.id.sign_in_email).text.toString(),
                    findViewById<EditText>(R.id.sign_in_password).text.toString()
                )
                App.networkApi.signIn(request).enqueue(object: Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        if (response.body()?.status == 404) {
                            AuthAnimator.showErrorMessage(
                                resources.getString(R.string.user_not_found),
                                resources.getDrawable(R.drawable.error_bg),
                                findViewById(R.id.sign_in_error),
                                listOf(findViewById(R.id.sign_in_email))
                            )
                        } else if (response.body()?.status == 506) {
                            AuthAnimator.showErrorMessage(
                                resources.getString(R.string.invalid_password),
                                resources.getDrawable(R.drawable.error_bg),
                                findViewById(R.id.sign_in_error),
                                listOf(findViewById(R.id.sign_in_password))
                            )
                        } else if (response.body()?.status == 200) {
                            UserData.id = response.body()?.userId.toString()
                            UserData.nickname = response.body()?.nickname.toString()
                            UserData.password = findViewById<EditText>(R.id.sign_in_password).text.toString()
                            UserData.email = findViewById<EditText>(R.id.sign_in_email).text.toString()

                            loadUserAvatar()
                        }
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
                findViewById<LinearLayout>(R.id.auth_load_indicator).visibility = View.VISIBLE
                val loadBar = findViewById<ImageView>(R.id.auth_load_bar)
                if (loadBar.drawable is Animatable) {
                    (loadBar.drawable as AnimatedVectorDrawable).start()
                }

                val password = findViewById<TextView>(R.id.sign_up_password)
                val repeatPassword = findViewById<TextView>(R.id.sign_up_repeat_password)

                if (password.text.toString() != repeatPassword.text.toString()) {
                    password.text = ""
                    repeatPassword.text = ""

                    AuthAnimator.showErrorMessage(
                        resources.getString(R.string.passwords_not_equals),
                        resources.getDrawable(R.drawable.error_bg),
                        findViewById(R.id.sign_up_error),
                        listOf(
                            findViewById(R.id.sign_up_password),
                            findViewById(R.id.sign_up_repeat_password)
                        )
                    )

                    return@setOnEditorActionListener false
                }

                UserData.nickname = findViewById<EditText>(R.id.sign_up_nickname).text.toString()
                UserData.email = findViewById<EditText>(R.id.sign_up_email).text.toString()
                UserData.password = password.text.toString()


                val request = AuthRequest.SignUp(
                    UserData.nickname,
                    UserData.email,
                    UserData.password
                )

                App.networkApi.signUp(request).enqueue(object: Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        if (response.body()?.status == 506) {
                            runOnUiThread {
                                AuthAnimator.showErrorMessage(
                                    resources.getText(R.string.user_already_exists).toString(),
                                    resources.getDrawable(R.drawable.error_bg),
                                    findViewById(R.id.sign_up_error),
                                    listOf(findViewById(R.id.sign_up_email))
                                )
                            }
                        } else {
                            UserData.id = response.body()?.userId.toString()

                            runOnUiThread { finishAuth() }
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }
            return@setOnEditorActionListener true
        }
    }

    private fun loadUserAvatar() {
        App.networkApi.getAvatar(UserData.id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
               val stream = response.body()?.byteStream()

                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888

                UserData.roundedAvatar = BitmapFactory.decodeStream(stream, null, options)

                saveAvatarToFile()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {}

        })
    }

    private fun saveAvatarToFile() {
        Thread {
            val fos = FileOutputStream("${filesDir.path}/user_avatar.png")
            val bos = ByteArrayOutputStream()
            UserData.roundedAvatar?.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val data = bos.toByteArray()
            bos.close()

            fos.write(data)
            fos.flush()
            fos.close()

            runOnUiThread {
                finishAuth()
            }
        }.start()
    }

    private fun finishAuth() {
        val preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("user_id", UserData.id)
        editor.putString("nickname", UserData.nickname)
        editor.putString("email", UserData.email)
        editor.putString("password", UserData.password)
        editor.apply()

        startActivity(Intent(this, LauncherActivity::class.java))
    }
}