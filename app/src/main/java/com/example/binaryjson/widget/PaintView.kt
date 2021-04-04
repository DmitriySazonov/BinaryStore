package com.example.binaryjson.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class PaintView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private data class Entry(
            val path: Path = Path(),
            val paint: Paint = Paint().apply {
                color = Color.BLUE
                style = Paint.Style.STROKE
                strokeWidth = 16f
            }
    )

    private val pathMap = HashMap<Int, Entry>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        pathMap.forEach { (_, entry) ->
            canvas.drawPath(entry.path, entry.paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointer = event.actionIndex
                val pId = event.getPointerId(pointer)
                val path = pathMap.getOrPut(pId) {
                    Entry().apply {
                        paint.color = Color.rgb(
                                Random.nextInt(0, 255),
                                Random.nextInt(0, 255),
                                Random.nextInt(0, 255)
                        )
                    }
                }.path
                kotlin.runCatching {
                    path.moveTo(event.getX(pId), event.getY(pId))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                (0 until event.pointerCount).forEach {
                    val pId = event.getPointerId(it)
                    val path = pathMap.getOrPut(pId) { Entry() }.path
                    kotlin.runCatching {
                        path.lineTo(event.getX(pId), event.getY(pId))
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointer = event.actionIndex
                val pId = event.getPointerId(pointer)
                pathMap.remove(pId)
            }
        }
        invalidate()
        return true
    }
}