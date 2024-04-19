package com.nakaharadev.roleworld

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import android.widget.ViewFlipper
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.nakaharadev.roleworld.controllers.InnerNotificationsController
import com.nakaharadev.roleworld.controllers.MenuController
import com.nakaharadev.roleworld.models.Character
import com.nakaharadev.roleworld.network.model.AddResponse
import com.nakaharadev.roleworld.network.model.UpdateRequest
import com.nakaharadev.roleworld.network.model.UpdateResponse
import com.nakaharadev.roleworld.ui.AnimatedImageView
import com.nakaharadev.roleworld.ui.CharacterLayout
import com.nakaharadev.roleworld.ui.ImageClipperView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AppActivity : Activity() {
    private val RESULT_GET_AVATAR = 1
    private val RESULT_GET_CHARACTER_AVATAR = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Converter.init(this)

        loadCharacters()
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

            loadUserAvatar(bitmap)
        } else if (requestCode == RESULT_GET_CHARACTER_AVATAR) {
            val uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            loadCharacterAvatar(bitmap)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadCharacterAvatar(avatar: Bitmap) {
        val clipper = findViewById<ImageClipperView>(R.id.image_clipper)
        clipper.setImage(avatar)

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

            findViewById<AnimatedImageView>(R.id.character_avatar).setImageBitmap(clipped)

            findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 0
        }

        findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 1
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

            if (UserData.roundedAvatar == null) {
                findViewById<AnimatedImageView>(R.id.profile_avatar).setImageBitmap(null, false)
            }

            UserData.roundedAvatar = clipped

            val loadBar = findViewById<ImageView>(R.id.load_avatar_bar)
            loadBar.visibility = View.VISIBLE
            if (loadBar.drawable is Animatable) {
                (loadBar.drawable as AnimatedVectorDrawable).start()
            }

            findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 0

            updateAvatar(UserData.roundedAvatar!!)
        }

        findViewById<ViewFlipper>(R.id.clipper_flipper).displayedChild = 1
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

    private fun loadCharacters() {
        val file = File("${filesDir.path}/characters.json")
        if (!file.exists()) return

        val stream = DataInputStream(FileInputStream(file))
        val jsonString = stream.readUTF()

        val gson = GsonBuilder().create()
        val array = gson.fromJson(jsonString, JsonArray::class.java)

        for (elem: JsonElement in array) {
            val character = Character()
            val obj = elem.asJsonObject
            character.id = obj["id"].asString
            character.name = obj["name"].asString

            val avatarFile = File("${filesDir.path}/${character.name}_avatar.png")
            if (avatarFile.exists()) {
                val inputStream = FileInputStream(avatarFile)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888

                character.avatar = BitmapFactory.decodeStream(inputStream, null, options)
            }

            if (character.avatar == null) {
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
            }

            UserData.characters[character.id] = character
            UserData.charactersId.add(character.id)
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

        val colors = HashMap<Int, Int>()
        colors[InnerNotificationsController.DONE] = getColor(R.color.green)
        colors[InnerNotificationsController.ERROR] = getColor(R.color.red)

        InnerNotificationsController.init(this, findViewById(R.id.inner_notification_view), colors)

        for (id: String in UserData.charactersId) {
            addCharacterToUI(UserData.characters[id]!!)
        }

        initMenu()
    }

    private fun initMenu() {
        if (UserData.roundedAvatar != null) {
            findViewById<AnimatedImageView>(R.id.open_profile).setImageBitmap(UserData.roundedAvatar, false)
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

            override fun onAfterChangeState(newState: Int, viewsMap: HashMap<String, View>?) {}
        })

        MenuController.addButtonCallback(R.id.open_profile, this::loadProfile)
        MenuController.addButtonCallback(R.id.open_chat, this::loadChat)
        MenuController.addButtonCallback(R.id.open_characters, this::loadCharactersView)
        MenuController.addButtonCallback(R.id.open_settings, this::loadSettings)

        MenuController.setMinAnimValue(0f)
        MenuController.setMaxAnimValue(70f)
        MenuController.setMenuLayout(findViewById(R.id.menu_layout))
        MenuController.setAnimDuration(300)
        MenuController.init(findViewById(R.id.menu_button))
    }

    private fun loadProfile() {
        if (UserData.roundedAvatar != null) {
            findViewById<AnimatedImageView>(R.id.profile_avatar).setImageBitmap(UserData.roundedAvatar, false)
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

        findViewById<TextView>(R.id.profile_account_exit).setOnClickListener {
            exitFromAccount()
        }

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

                                runOnUiThread {
                                    InnerNotificationsController.showNotification(
                                        getString(R.string.done),
                                        InnerNotificationsController.DONE
                                    )
                                }
                            }

                            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                                runOnUiThread {
                                    InnerNotificationsController.showNotification(
                                        getString(R.string.cant_update_nickname),
                                        InnerNotificationsController.ERROR
                                    )
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

    private fun exitFromAccount() {
        val flipper = findViewById<ViewFlipper>(R.id.navbar_flipper)
        val currentDisplayedChild = flipper.displayedChild

        flipper.displayedChild = flipper.childCount - 1

        findViewById<ImageView>(R.id.navbar_account_exit_ok).setOnClickListener {
            val preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.remove("nickname")
            editor.apply()

            val avatarFile = File("${filesDir.path}/user_avatar.png")
            if (avatarFile.exists()) {
                avatarFile.delete()
            }

            startActivity(Intent(this, LauncherActivity::class.java))
        }

        findViewById<ImageView>(R.id.navbar_account_exit_close).setOnClickListener {
            flipper.displayedChild = currentDisplayedChild
        }
    }

    private fun loadChat() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 0
    }

    private fun loadCharactersView() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 2

        findViewById<ImageView>(R.id.new_character).setOnClickListener {
            val navBarFlipper = findViewById<ViewFlipper>(R.id.navbar_flipper)
            val currentDisplayed = navBarFlipper.displayedChild
            navBarFlipper.displayedChild = navBarFlipper.childCount - 2

            findViewById<AnimatedImageView>(R.id.character_avatar).setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, RESULT_GET_CHARACTER_AVATAR)
            }

            findViewById<EditText>(R.id.input_character_nickname).setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val character = Character()
                    character.name = v.text.toString()
                    character.avatar = (findViewById<ImageView>(R.id.character_avatar).drawable as BitmapDrawable).bitmap

                    val cache = File("${cacheDir.path}/character_avatar")
                    cache.createNewFile()

                    val bos = ByteArrayOutputStream()
                    character.avatar?.compress(Bitmap.CompressFormat.PNG, 0, bos)
                    val data = bos.toByteArray()

                    val fos = FileOutputStream(cache)
                    fos.write(data)
                    fos.flush()
                    fos.close()

                    val requestFile = cache.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("character_avatar", cache.name, requestFile)

                    App.networkApi.addCharacter(UserData.id, character.name, body).enqueue(
                        object : Callback<AddResponse> {
                            override fun onResponse(
                                call: Call<AddResponse>,
                                response: Response<AddResponse>
                            ) {
                                character.id = response.body()?.id!!

                                val charactersFile = File("${filesDir.path}/characters.json")
                                if (!charactersFile.exists()) {
                                    charactersFile.createNewFile()

                                    val array = JsonArray()

                                    val gson = GsonBuilder().create()
                                    array.add(gson.toJsonTree(character.toSerializable()))

                                    val json = gson.toJson(array)

                                    val stream = DataOutputStream(FileOutputStream(charactersFile))
                                    stream.writeUTF(json)
                                    stream.flush()
                                    stream.close()
                                } else {
                                    val stream = DataInputStream(FileInputStream(charactersFile))
                                    var json = stream.readUTF()
                                    stream.close()

                                    val gson = GsonBuilder().create()
                                    val array = gson.fromJson(json, JsonArray::class.java)
                                    array.add(gson.toJsonTree(character.toSerializable()))

                                    json = gson.toJson(array)

                                    val outputStream = DataOutputStream(FileOutputStream(charactersFile))
                                    outputStream.writeUTF(json)
                                    outputStream.flush()
                                    outputStream.close()
                                }

                                saveCharacterAvatar(character.avatar!!, character.name)
                                UserData.characters[character.id] = character

                                runOnUiThread {
                                    InnerNotificationsController.showNotification(
                                        getString(R.string.done),
                                        InnerNotificationsController.DONE
                                    )
                                    navBarFlipper.displayedChild = currentDisplayed
                                    addCharacterToUI(character)
                                }
                            }

                            override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                                runOnUiThread {
                                    InnerNotificationsController.showNotification(
                                        getString(R.string.error),
                                        InnerNotificationsController.ERROR
                                    )
                                }
                            }
                        }
                    )
                }

                return@setOnEditorActionListener true
            }
        }
    }

    private fun addCharacterToUI(character: Character) {
        val list = findViewById<LinearLayout>(R.id.characters_list)
        val profileList = findViewById<LinearLayout>(R.id.profile_characters_list)

        list.removeView(findViewById(R.id.characters_list_empty))
        profileList.removeView(findViewById(R.id.profile_characters_list_empty))

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.character_view, null) as CharacterLayout
        view.setCharacterId(character.id)
        view.setOnClickListener {

        }
        view.findViewById<ImageView>(R.id.character_avatar).setImageBitmap(character.avatar)
        view.findViewById<TextView>(R.id.character_name).text = character.name

        val profileView = inflater.inflate(R.layout.character_view, null) as CharacterLayout
        profileView.setCharacterId(character.id)
        profileView.setOnClickListener {

        }
        profileView.findViewById<ImageView>(R.id.character_avatar).setImageBitmap(character.avatar)
        profileView.findViewById<TextView>(R.id.character_name).text = character.name

        list.addView(view)
        profileList.addView(profileView)
    }

    private fun loadSettings() {
        findViewById<ViewFlipper>(R.id.main_flipper).displayedChild = 3
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

    private fun saveCharacterAvatar(avatar: Bitmap, name: String) {
        Thread {
            val fos = FileOutputStream("${filesDir.path}/${name}_avatar.png")
            val bos = ByteArrayOutputStream()
            avatar.compress(Bitmap.CompressFormat.PNG, 0, bos)
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
                    val loadBar = findViewById<ImageView>(R.id.load_avatar_bar)
                    if (loadBar.drawable is Animatable) {
                        (loadBar.drawable as AnimatedVectorDrawable).stop()
                    }
                    loadBar.visibility = View.GONE

                    findViewById<ImageView>(R.id.profile_avatar).setImageBitmap(UserData.roundedAvatar)
                    findViewById<ImageView>(R.id.open_profile).setImageBitmap(UserData.roundedAvatar)

                    saveAvatar(UserData.roundedAvatar!!)

                    InnerNotificationsController.showNotification(
                        getString(R.string.done),
                        InnerNotificationsController.DONE
                    )
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                runOnUiThread {
                    InnerNotificationsController.showNotification(
                        getString(R.string.cant_update_avatar),
                        InnerNotificationsController.ERROR
                    )
                }
            }
        })
    }
}