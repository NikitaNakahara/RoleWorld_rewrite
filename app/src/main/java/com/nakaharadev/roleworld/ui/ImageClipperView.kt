package com.nakaharadev.roleworld.ui

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.nakaharadev.roleworld.Converter
import kotlin.math.abs
import kotlin.math.sqrt

class ImageClipperView
    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0,
                defStyleRes: Int = 0
    ) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var image: Bitmap? = null

    private var imageX = 0
    private var imageY = 0
    private var imageWidth = 0
    private var imageHeight = 0

    private var isInitDraw = true

    private var clipperRadius = 0

    private var clipperRoundImg: Bitmap? = null

    private var xPos = 0f
    private var yPos = 0f

    private var oldSeekValue = 0

    fun setImage(image: Bitmap) {
        this.image = image
        isInitDraw = true
    }

    fun changeSize(offset: Int) {
        changeImageSize(offset)

        invalidate()
    }

    fun getClipped(): Bitmap {
        isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(drawingCache)
        isDrawingCacheEnabled = false

        val clipped = Bitmap.createBitmap(bitmap,
            width / 2 - clipperRadius,
            height / 2 - clipperRadius,
            clipperRadius * 2,
            clipperRadius * 2)

        val result = Bitmap.createBitmap(clipped.width, clipped.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)
        val paint = Paint()
        val rect = Rect(0, 0, clipped.height, clipped.height)

        paint.isAntiAlias = true
        paint.setColor(Color.GREEN)

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(clipped.width / 2f, clipped.height / 2f, clipperRadius.toFloat(), paint)

        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        canvas.drawBitmap(clipped, rect, rect, paint)

        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (isInitDraw) {
            initClipperRadius()
            initImageSize()
            initClipperImg()

            isInitDraw = false
        }

        canvas.drawBitmap(image!!, null, Rect(imageX, imageY, imageX + imageWidth, imageY + imageHeight), null)

        canvas.drawBitmap(clipperRoundImg!!, null, Rect(0, 0, width, height), null)
    }

    private fun initClipperRadius() {
        clipperRadius = if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            (height - Converter.dpToPx(40f).toInt()) / 2
        } else {
            (width - Converter.dpToPx(40f).toInt()) / 2
        }
    }

    private fun initClipperImg() {
        val defaultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(defaultBitmap)
        val paint = Paint()

        paint.setColor(Color.TRANSPARENT)
        paint.isAntiAlias = true
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_OUT))

        canvas.drawColor(Color.parseColor("#55000000"))
        canvas.drawCircle(width / 2f, height / 2f, clipperRadius.toFloat(), paint)

        clipperRoundImg = defaultBitmap
    }

    private fun initImageSize() {
        if (image?.width!! == image?.height!!) {
            imageX = width / 2 - clipperRadius
            imageY = height / 2 - clipperRadius
            imageWidth = clipperRadius * 2
            imageHeight = clipperRadius * 2
        } else if (image?.width!! > image?.height!!) {
            imageY = height / 2 - clipperRadius
            imageHeight = clipperRadius * 2
            val ratio = imageHeight.toFloat() / image?.height!!
            imageWidth = (image?.width!! * ratio).toInt()
            imageX = width / 2 - imageWidth / 2
        } else {
            imageX = width / 2 - clipperRadius
            imageWidth = clipperRadius * 2
            val ratio = imageWidth.toFloat() / image?.width!!
            imageHeight = (image?.height!! * ratio).toInt()
            imageY = height / 2 - imageHeight / 2
        }
    }

    private fun setNewImagePos(xOffset: Float, yOffset: Float) {
        imageX += xOffset.toInt()
        imageY += yOffset.toInt()
    }

    private fun changeImageSize(offset: Int) {
        if (imageWidth > imageHeight) {
            if (imageHeight + offset < clipperRadius * 2) return
        } else {
            if (imageWidth + offset < clipperRadius * 2) return
        }

        if (imageX + imageWidth + offset < width / 2 + clipperRadius) return
        if (imageY + imageHeight + offset < height / 2 + clipperRadius) return

        if (imageWidth > imageHeight) {
            val ratio = imageHeight / imageWidth.toFloat()

            imageWidth = (imageWidth + offset / ratio).toInt()
            imageHeight += offset
        } else if (imageWidth < imageHeight) {
            val ratio = imageWidth / imageHeight.toFloat()

            imageWidth += offset
            imageHeight = (imageHeight + offset / ratio).toInt()
        } else {
            imageWidth += offset
            imageHeight += offset
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                xPos = event.x
                yPos = event.y

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.x - xPos
                val deltaY = event.y - yPos

                xPos = event.x
                yPos = event.y

                setNewImagePos(deltaX, deltaY)

                if (imageX > width / 2 - clipperRadius) {
                    imageX = width / 2 - clipperRadius
                }
                if (imageY > height / 2 - clipperRadius) {
                    imageY = height / 2 - clipperRadius
                }
                if (imageX + imageWidth < width / 2 + clipperRadius) {
                    imageX = width / 2 + clipperRadius - imageWidth
                }
                if (imageY + imageHeight < height / 2 + clipperRadius) {
                    imageY = height / 2 + clipperRadius - imageHeight
                }

                invalidate()

                return true
            }

            MotionEvent.ACTION_UP -> {
                return true
            }
        }

        return false
    }
}