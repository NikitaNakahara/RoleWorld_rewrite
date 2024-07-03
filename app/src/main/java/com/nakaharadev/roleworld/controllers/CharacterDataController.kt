package com.nakaharadev.roleworld.controllers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nakaharadev.roleworld.Converter
import com.nakaharadev.roleworld.R
import com.nakaharadev.roleworld.file_tasks.UpdateCharactersFileTask
import com.nakaharadev.roleworld.models.Character
import com.nakaharadev.roleworld.network.model.requests.ValueRequest
import com.nakaharadev.roleworld.network.tasks.UpdateCharacterDataTask
import com.nakaharadev.roleworld.services.FileManagerService
import com.nakaharadev.roleworld.services.NetworkService
import com.nakaharadev.roleworld.ui.AnimatedImageView

@SuppressLint("StaticFieldLeak")
object CharacterDataController {
    private lateinit var layout: LinearLayout
    lateinit var characterObj: Character
    private lateinit var callback: (v: ImageView, c: Character) -> Unit
    private lateinit var runOnUi: (Runnable) -> Unit
    private lateinit var ctx: Context

    private val SEX_MAN = "man"
    private val SEX_WOMAN = "woman"
    private val SEX_HERMAPHRODITE = "hermaphrodite"


    fun init(context: Context, dataLayout: LinearLayout, character: Character, runOnUiThread: (Runnable) -> Unit, onGetAvatarCallback: (v: ImageView, c: Character) -> Unit) {
        layout = dataLayout
        characterObj = character
        callback = onGetAvatarCallback
        ctx = context
        runOnUi = runOnUiThread

        initCharacterFields()
        initAvatarChange()
        initNameChange()
        initDataChange()
        initSexChange()
    }


    private fun initCharacterFields() {
        layout.findViewById<AnimatedImageView>(R.id.character_data_avatar).setImageBitmap(characterObj.avatar!!, false)
        layout.findViewById<TextView>(R.id.character_data_name).text = characterObj.name

        if (characterObj.bio.isNotEmpty()) {
            layout.findViewById<TextView>(R.id.character_bio).text = characterObj.bio
        }

        if (characterObj.desc.isNotEmpty()) {
            layout.findViewById<TextView>(R.id.character_description).text = characterObj.desc
        }

        if (characterObj.sex == SEX_MAN) {
            layout.findViewById<ImageView>(R.id.character_sex_man).imageTintList = null
        } else if (characterObj.sex == SEX_WOMAN) {
            layout.findViewById<ImageView>(R.id.character_sex_woman).imageTintList = null
        } else if (characterObj.sex == SEX_HERMAPHRODITE) {
            layout.findViewById<ImageView>(R.id.character_sex_hermaphrodite).imageTintList = null
        }
    }

    private fun initAvatarChange() {
        layout.findViewById<AnimatedImageView>(R.id.character_data_avatar).setOnLongClickListener {
            it as ImageView

            callback(it, characterObj)

            return@setOnLongClickListener true
        }
    }

    private fun initNameChange() {
        layout.findViewById<EditText>(R.id.character_data_name).setOnLongClickListener {
            it as EditText

            it.setBackgroundResource(R.drawable.current_edit_profile_field)

            it.isFocusable = true
            it.isFocusableInTouchMode = true
            it.isClickable = true

            it.requestFocus()
            (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)

            it.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val newName = it.text.toString()
                    NetworkService.addTask(UpdateCharacterDataTask(
                        ValueRequest(newName),
                        UpdateCharacterDataTask.DATA_TYPE_NAME,
                        characterObj.id
                    )) { _ ->
                        runOnUi(Runnable {
                            val newCharacter = characterObj
                            newCharacter.name = newName

                            it.isFocusable = false
                            it.isFocusableInTouchMode = false
                            it.isClickable = false

                            it.setBackgroundColor(ctx.getColor(R.color.transparent))

                            (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                                .hideSoftInputFromWindow(it.windowToken, 0)

                            InnerNotificationsController.showNotification(ctx.getString(R.string.done), InnerNotificationsController.DONE)

                            FileManagerService.addTask(UpdateCharactersFileTask(
                                "${ctx.filesDir.path}/characters.json",
                                newCharacter,
                                UpdateCharactersFileTask.UPDATE_MODE_CHANGE
                            ))
                        })
                    }

                    return@setOnEditorActionListener true
                }

                return@setOnEditorActionListener false
            }

            return@setOnLongClickListener true
        }
    }

    private fun initSexChange() {
        val manBtn = layout.findViewById<ImageView>(R.id.character_sex_man)
        val womanBtn = layout.findViewById<ImageView>(R.id.character_sex_woman)
        val hermaphroditeBtn = layout.findViewById<ImageView>(R.id.character_sex_hermaphrodite)

        manBtn.setOnClickListener {
            womanBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            hermaphroditeBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            manBtn.imageTintList = null

            sendNewSex(SEX_MAN)
        }

        womanBtn.setOnClickListener {
            manBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            hermaphroditeBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            womanBtn.imageTintList = null

            sendNewSex(SEX_WOMAN)
        }

        hermaphroditeBtn.setOnClickListener {
            manBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            womanBtn.imageTintList = ColorStateList.valueOf(ctx.getColor(R.color.gray_text))
            hermaphroditeBtn.imageTintList = null

            sendNewSex(SEX_HERMAPHRODITE)
        }
    }

    private fun initDataChange() {
        var currentChangingField: String? = null

        layout.findViewById<ImageView>(R.id.character_data_change_desc).setOnClickListener {
            it as ImageView

            val edit = layout.findViewById<EditText>(R.id.character_description)

            if (currentChangingField == "bio") {
                sendNewBio(layout.findViewById<EditText>(R.id.character_bio).text.toString(), layout.findViewById(R.id.character_data_change_bio))
            } else if (currentChangingField == "desc") {
                currentChangingField = null

                edit.isFocusable = false
                edit.isFocusableInTouchMode = false
                edit.isClickable = false

                (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(edit.windowToken, 0)

                sendNewDesc(edit.text.toString(), it)

                return@setOnClickListener
            }

            currentChangingField = "desc"

            it.setImageResource(R.drawable.minim_done_icon)
            it.setPadding(
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt()
            )

            edit.isFocusable = true
            edit.isFocusableInTouchMode = true
            edit.isClickable = true

            edit.requestFocus()
            (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)

        }

        layout.findViewById<ImageView>(R.id.character_data_change_bio).setOnClickListener {
            it as ImageView

            val edit = layout.findViewById<EditText>(R.id.character_bio)

            if (currentChangingField == "desc") {
                sendNewDesc(layout.findViewById<EditText>(R.id.character_description).text.toString(), layout.findViewById(R.id.character_data_change_desc))
            } else if (currentChangingField == "bio") {
                currentChangingField = null

                edit.isFocusable = false
                edit.isFocusableInTouchMode = false
                edit.isClickable = false

                (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(edit.windowToken, 0)

                sendNewBio(edit.text.toString(), it)

                return@setOnClickListener
            }

            currentChangingField = "bio"

            it.setImageResource(R.drawable.minim_done_icon)
            it.setPadding(
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt(),
                Converter.dpToPx(4f).toInt()
            )

            edit.isFocusable = true
            edit.isFocusableInTouchMode = true
            edit.isClickable = true

            edit.requestFocus()
            (ctx.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun sendNewBio(data: String, btn: ImageView) {
        btn.setImageResource(R.drawable.round_load_bar)
        (btn.drawable as AnimatedVectorDrawable).start()

        NetworkService.addTask(UpdateCharacterDataTask(
            ValueRequest(data),
            UpdateCharacterDataTask.DATA_TYPE_BIO,
            characterObj.id
        )) {
            characterObj.bio = data
            FileManagerService.addTask(UpdateCharactersFileTask(
                "${ctx.filesDir.path}/characters.json",
                characterObj,
                UpdateCharactersFileTask.UPDATE_MODE_CHANGE
            ))

            runOnUi(Runnable {
                btn.setImageResource(R.drawable.change_icon)
                btn.setPadding(
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt()
                )
            })
        }
    }

    private fun sendNewDesc(data: String, btn: ImageView) {
        btn.setImageResource(R.drawable.round_load_bar)
        (btn.drawable as AnimatedVectorDrawable).start()

        NetworkService.addTask(UpdateCharacterDataTask(
            ValueRequest(data),
            UpdateCharacterDataTask.DATA_TYPE_DESC,
            characterObj.id
        )) {
            characterObj.desc = data
            FileManagerService.addTask(UpdateCharactersFileTask(
                "${ctx.filesDir.path}/characters.json",
                characterObj,
                UpdateCharactersFileTask.UPDATE_MODE_CHANGE
            ))

            runOnUi(Runnable {
                btn.setImageResource(R.drawable.change_icon)
                btn.setPadding(
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt(),
                    Converter.dpToPx(8f).toInt()
                )

                InnerNotificationsController.showNotification(ctx.getString(R.string.done), InnerNotificationsController.DONE)
            })
        }
    }

    private fun sendNewSex(value: String) {
        NetworkService.addTask(UpdateCharacterDataTask(
            ValueRequest(value),
            UpdateCharacterDataTask.DATA_TYPE_SEX,
            characterObj.id
        )) {
            characterObj.sex = value
            FileManagerService.addTask(UpdateCharactersFileTask(
                "${ctx.filesDir.path}/characters.json",
                characterObj,
                UpdateCharactersFileTask.UPDATE_MODE_CHANGE
            ))

            runOnUi(Runnable {
                InnerNotificationsController.showNotification(ctx.getString(R.string.done), InnerNotificationsController.DONE)
            })
        }
    }
}