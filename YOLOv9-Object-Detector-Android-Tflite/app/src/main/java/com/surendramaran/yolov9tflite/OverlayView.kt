package com.surendramaran.yolov9tflite

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        results = listOf()
        textPaint.reset()
        textBackgroundPaint.reset()
        boxPaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        boxPaint.color = ContextCompat.getColor(context!!, R.color.dodo)
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    //None of cnf

//    override fun draw(canvas: Canvas) {
//        super.draw(canvas)
//
//        results.forEach {
//            val left = it.x1 * width
//            val top = it.y1 * height
//            val right = it.x2 * width
//            val bottom = it.y2 * height
//
//            canvas.drawRect(left, top, right, bottom, boxPaint)
//            val drawableText = it.clsName
//
//            textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
//            val textWidth = bounds.width()
//            val textHeight = bounds.height()
//            canvas.drawRect(
//                left,
//                top,
//                left + textWidth + BOUNDING_RECT_TEXT_PADDING,
//                top + textHeight + BOUNDING_RECT_TEXT_PADDING,
//                textBackgroundPaint
//            )
//            canvas.drawText(drawableText, left, top + bounds.height(), textPaint)
//
//        }
//    }

    //Draw with cnf
//override fun draw(canvas: Canvas) {
//    super.draw(canvas)
//
//    results.forEach {
//        val left = it.x1 * width
//        val top = it.y1 * height
//        val right = it.x2 * width
//        val bottom = it.y2 * height
//
//        // Vẽ bounding box
//        canvas.drawRect(left, top, right, bottom, boxPaint)
//
//        // 🔹 Thêm độ tin cậy (%)
//        val confidenceText = "${it.clsName} (${String.format("%.2f", it.cnf * 100)}%)"
//
//        // Vẽ nền cho text
//        textBackgroundPaint.getTextBounds(confidenceText, 0, confidenceText.length, bounds)
//        val textWidth = bounds.width()
//        val textHeight = bounds.height()
//        canvas.drawRect(
//            left,
//            top,
//            left + textWidth + BOUNDING_RECT_TEXT_PADDING,
//            top + textHeight + BOUNDING_RECT_TEXT_PADDING,
//            textBackgroundPaint
//        )
//
//        // Vẽ text
//        canvas.drawText(confidenceText, left, top + bounds.height(), textPaint)
//    }
//}

    //Draw with cnf > 0.6

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results.forEach {
            // 🔹 Chỉ vẽ nếu độ tin cậy > 50%
            if (it.cnf > 0.6) {
                val left = it.x1 * width
                val top = it.y1 * height
                val right = it.x2 * width
                val bottom = it.y2 * height

                // Vẽ bounding box
                canvas.drawRect(left, top, right, bottom, boxPaint)

                // Thêm text: "Tên lớp (confidence%)"
                val confidenceText = "${it.clsName} (${String.format("%.2f", it.cnf * 100)}%)"

                // Vẽ nền cho chữ
                textBackgroundPaint.getTextBounds(confidenceText, 0, confidenceText.length, bounds)
                val textWidth = bounds.width()
                val textHeight = bounds.height()
                canvas.drawRect(
                    left,
                    top,
                    left + textWidth + BOUNDING_RECT_TEXT_PADDING,
                    top + textHeight + BOUNDING_RECT_TEXT_PADDING,
                    textBackgroundPaint
                )

                // Vẽ chữ
                canvas.drawText(confidenceText, left, top + bounds.height(), textPaint)
            }
        }
    }



    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 8
    }
}