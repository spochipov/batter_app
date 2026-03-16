package com.batterapp.matrixcode.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * Matrix-style falling characters rain (green on black).
 */
class MatrixRainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ .,!?\";:()[]{}<>/|\\+-*=%#@&^~`".toCharArray()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF00FF41.toInt()
        textSize = 36f
        typeface = android.graphics.Typeface.MONOSPACE
    }
    private val paintDim = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x8000FF41.toInt()
        textSize = 36f
        typeface = android.graphics.Typeface.MONOSPACE
    }

    private data class Column(
        var y: Float,
        var speed: Float,
        val symbols: CharArray
    )

    private var columns: List<Column> = emptyList()
    private var columnWidth: Float = 0f
    private var charHeight: Float = 0f

    private val animator = object : Runnable {
        override fun run() {
            updateColumns()
            invalidate()
            postOnAnimation(this)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) return
        charHeight = paint.descent() - paint.ascent()
        columnWidth = paint.measureText("0").coerceAtLeast(1f)
        val count = (w / columnWidth).toInt().coerceAtLeast(8)
        columns = (0 until count).map {
            Column(
                y = Random.nextFloat() * h,
                speed = 8f + Random.nextFloat() * 12f,
                symbols = CharArray((h / charHeight).toInt().coerceAtLeast(10)) {
                    chars[Random.nextInt(chars.size)]
                }
            )
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postOnAnimation(animator)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(animator)
    }

    private fun updateColumns() {
        val h = height.toFloat()
        columns.forEach { col ->
            col.y += col.speed
            if (col.y > h + col.symbols.size * charHeight) {
                col.y = -col.symbols.size * charHeight
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val h = height.toFloat()
        columns.forEachIndexed { index, col ->
            val x = index * columnWidth
            col.symbols.forEachIndexed { i, ch ->
                val y = col.y + i * charHeight
                if (y in -charHeight..h + charHeight) {
                    val p = if (i == col.symbols.lastIndex) paint else paintDim
                    canvas.drawText(ch.toString(), x, y - paint.ascent(), p)
                }
            }
        }
    }
}
