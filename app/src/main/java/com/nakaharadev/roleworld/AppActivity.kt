package com.nakaharadev.roleworld

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import android.widget.ViewFlipper
import com.nakaharadev.roleworld.controllers.MenuController
import com.nakaharadev.roleworld.network.model.UpdateRequest
import com.nakaharadev.roleworld.network.model.UpdateResponse
import com.nakaharadev.roleworld.ui.ImageClipperView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AppActivity : Activity() {
    private val RESULT_GET_AVATAR = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Converter.init(this)

        loadAvatar()
        initMainView()
    }

    override fun onStart() {
        super.onStart()

        initBg()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) return
        if (data == null) return

        if (requestCode == RESULT_GET_AVATAR) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 0

            loadUserAvatar(bitmap)
        }
    }

    private fun loadAvatar() {
        val file = File("${filesDir.path}/user_avatar.png")
        if (file.exists()) {
            val stream = FileInputStream(file)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            UserData.roundedAvatar = BitmapFactory.decodeStream(stream, null, options)
        }
    }

    private fun initBg() {
        val view = findViewById<VideoView>(R.id.main_layout_bg)

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

    private fun initMainView() {
        setContentView(R.layout.main_layout)

        initMenu()
    }

    private fun initMenu() {
        if (UserData.roundedAvatar != null) {
            findViewById<ImageView>(R.id.open_profile).setImageBitmap(UserData.roundedAvatar)
        }

        MenuController.setCallback(object: MenuController.MenuStateCallback() {
            override fun onBeforeChangeState(oldState: Int): HashMap<String, View>? {
                val map = HashMap<String, View>()
                map["menu"] = findViewById(R.id.menu_layout)
                map["main"] = findViewById(R.id.main_view_layout)
                map["navBar"] = findViewById(R.id.navigation_bar)
                map["bg"] = findViewById(R.id.main_layout_bg)

                if (oldState == MenuController.MENU_STATE_CLOSE) {
                    findViewById<ImageView>(R.id.menu_button).setImageDrawable(resources.getDrawable(R.drawable.menu_icon))
                } else {
                    findViewById<ImageView>(R.id.menu_button).setImageDrawable(resources.getDrawable(R.drawable.opened_menu_icon))
                }

                return map
            }

            override fun onChangeAnimValue(value: Float, viewsMap: HashMap<String, View>?) {
                viewsMap?.get("menu")?.translationX = value - MenuController.getMaxValue()

                var params = viewsMap?.get("main")?.layoutParams as LinearLayout.LayoutParams
                params.marginStart = (value + (value / (MenuController.getMaxValue() / Converter.dpToPx(10f)))).toInt()
                params.topMargin = value.toInt()
                params.marginEnd = (value / (MenuController.getMaxValue() / Converter.dpToPx(10f))).toInt()
                params.bottomMargin = (value / (MenuController.getMaxValue() / Converter.dpToPx(10f))).toInt()
                viewsMap["main"]?.layoutParams = params
                viewsMap["main"]?.background?.alpha = ((1.0f - (value / MenuController.getMaxValue() * .3f)) * 255).toInt()
                viewsMap["main"]?.setPadding(0, (MenuController.getMaxValue() - value).toInt(), 0, 0)

                params = viewsMap["navBar"]?.layoutParams as LinearLayout.LayoutParams
                params.marginStart = (value + (value / (MenuController.getMaxValue() / Converter.dpToPx(10f)))).toInt()
                params.bottomMargin = (value / (MenuController.getMaxValue() / Converter.dpToPx(10f))).toInt()
                params.marginEnd = (value / (MenuController.getMaxValue() / Converter.dpToPx(10f))).toInt()
                viewsMap["navBar"]?.layoutParams = params

                val drawable = (viewsMap["navBar"]?.background as StateListDrawable).getStateDrawable(0) as GradientDrawable
                val cornerValue = value / MenuController.getMaxValue() * Converter.dpToPx(15f)
                drawable.cornerRadii = floatArrayOf(Converter.dpToPx(15f), Converter.dpToPx(15f), Converter.dpToPx(15f), Converter.dpToPx(15f), cornerValue, cornerValue, cornerValue, cornerValue, Converter.dpToPx(15f), Converter.dpToPx(15f), Converter.dpToPx(15f), Converter.dpToPx(15f))

                viewsMap["bg"]?.alpha = value / MenuController.getMaxValue()
            }

            override fun onAfterChangeState(newState: Int, viewsMap: HashMap<String, View>?) {

            }
        })

        MenuController.addButtonCallback(R.id.open_profile, this::loadProfile)
        MenuController.addButtonCallback(R.id.open_chat, this::loadChat)
        MenuController.addButtonCallback(R.id.open_characters, this::loadCharacters)
        MenuController.addButtonCallback(R.id.open_settings, this::loadSettings)

        MenuController.setMinAnimValue(0f)
        MenuController.setMaxAnimValue(70f)
        MenuController.setMenuLayout(findViewById(R.id.menu_layout))
        MenuController.setAnimDuration(300)
        MenuController.init(findViewById(R.id.menu_button))
    }

    private fun loadProfile() {
        if (UserData.roundedAvatar != null) {
            findViewById<ImageView>(R.id.profile_avatar).setImageBitmap(UserData.roundedAvatar)
        }

        findViewById<TextView>(R.id.profile_nickname).text = UserData.nickname
        findViewById<TextView>(R.id.profile_email).text = UserData.email
        val spannableStr = SpannableString(UserData.id)
        spannableStr.setSpan(UnderlineSpan(), 0, spannableStr.length, 0)
        findViewById<TextView>(R.id.profile_id).text = spannableStr

        findViewById<TextView>(R.id.profile_id).setOnLongClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("user_id", (it as TextView).text)
            clipboardManager.setPrimaryClip(clipData)

            return@setOnLongClickListener true
        }

        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 1

        findViewById<ImageView>(R.id.profile_avatar).setOnLongClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, RESULT_GET_AVATAR)

            return@setOnLongClickListener true
        }

        findViewById<EditText>(R.id.profile_nickname).setOnLongClickListener { nicknameView ->
            nicknameView as EditText

            nicknameView.isFocusable = true
            nicknameView.isFocusableInTouchMode = true
            nicknameView.isClickable = true

            nicknameView.setBackgroundResource(R.drawable.current_edit_profile_field)

            nicknameView.requestFocus()
            (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(nicknameView, InputMethodManager.SHOW_IMPLICIT)

            nicknameView.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    UserData.nickname = nicknameView.text.toString()

                    nicknameView.isFocusable = false
                    nicknameView.isFocusableInTouchMode = false
                    nicknameView.isClickable = false

                    nicknameView.setBackgroundColor(resources.getColor(R.color.window_bg))

                    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(nicknameView.windowToken, 0)

                    App.networkApi.updateNickname(UserData.id, UpdateRequest(UserData.nickname)).enqueue(
                        object : Callback<UpdateResponse> {
                            override fun onResponse(
                                call: Call<UpdateResponse>,
                                response: Response<UpdateResponse>
                            ) {
                                val preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("nickname", UserData.nickname)
                                editor.apply()
                            }

                            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                                runOnUiThread {
                                    Toast.makeText(this@AppActivity, "не удалось обновить ник", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }

                return@setOnEditorActionListener true
            }

            return@setOnLongClickListener true
        }
    }

    private fun loadChat() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 0
    }

    private fun loadCharacters() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 2
    }

    private fun loadSettings() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 3
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadUserAvatar(bitmap: Bitmap) {
        val clipper = findViewById<ImageClipperView>(R.id.image_clipper)
        clipper.setImage(bitmap)

        var xPos = 0f

        findViewById<RelativeLayout>(R.id.clipper_scale_swiper).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    xPos = event.x

                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_MOVE -> {
                    val delta = event.x - xPos
                    xPos = event.x

                    clipper.changeSize(delta.toInt() * 2)

                    return@setOnTouchListener true
                }

                MotionEvent.ACTION_UP -> return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }

        findViewById<ImageView>(R.id.clipper_back_button).setOnClickListener {
            findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 0
        }

        findViewById<ImageView>(R.id.clipper_done_button).setOnClickListener {
            val clipped = clipper.getClipped()

            UserData.roundedAvatar = clipped

            updateAvatar(UserData.roundedAvatar!!)
        }

        findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 1
    }

    private fun saveAvatar(bitmap: Bitmap) {
        Thread {
            val fos = FileOutputStream("${filesDir.path}/user_avatar.png")
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val data = bos.toByteArray()
            bos.close()

            fos.write(data)
            fos.flush()
            fos.close()
        }.start()
    }

    private fun updateAvatar(bitmap: Bitmap) {
        val cache = File("${cacheDir.path}/avatar")
        cache.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
        val data = bos.toByteArray()

        val fos = FileOutputStream(cache)
        fos.write(data)
        fos.flush()
        fos.close()

        val requestFile = cache.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("avatar", cache.name, requestFile)

        val response = App.networkApi.updateAvatar(UserData.id, body)
        response.enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(
                call: Call<UpdateResponse>,
                response: Response<UpdateResponse>
            ) {
                runOnUiThread {
                    findViewById<ImageView>(R.id.profile_avatar).setImageBitmap(UserData.roundedAvatar)
                    findViewById<ImageView>(R.id.open_profile).setImageBitmap(UserData.roundedAvatar)

                    saveAvatar(UserData.roundedAvatar!!)
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                runOnUiThread {
                    Toast.makeText(this@AppActivity, "не удалось загрузить аватар", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}