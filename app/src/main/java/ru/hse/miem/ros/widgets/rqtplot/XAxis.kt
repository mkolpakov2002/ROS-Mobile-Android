package ru.hse.miem.ros.widgets.rqtplot

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import ru.hse.miem.ros.widgets.rqtplot.PlotDataList.PlotData
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 03.06.21
 */
class XAxis(textSize: Float) {
    var textPaint: TextPaint
    var axisPaint: Paint = Paint()
    var linePaint: Paint
    lateinit var scales: MutableList<Float>
    var scale: Float = 1.1f
    var scaleAlpha: Int = 0
    var textSpace: Float
    var textWidth: Float
    var textHeight: Float
    var axisSpaceX: Float
    var axisSpaceY: Float

    init {
        axisPaint.color = Color.WHITE
        axisPaint.strokeWidth = 3f
        axisPaint.style = Paint.Style.STROKE
        textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
        val dashPathEffect = DashPathEffect(floatArrayOf(5f, 10f), 0f)
        linePaint = Paint()
        linePaint.color = Color.GRAY
        linePaint.strokeWidth = 2f
        linePaint.style = Paint.Style.STROKE
        linePaint.setPathEffect(dashPathEffect)
        val bounds = Rect()
        val textText = "WWW"
        textPaint.getTextBounds(textText, 0, textText.length, bounds)
        textWidth = bounds.width().toFloat()
        textHeight = bounds.height().toFloat()
        axisSpaceX = textHeight + 20
        axisSpaceY = textWidth + 20
        textSpace = (axisSpaceX - textHeight) / 2f
        updateScales()
    }

    fun draw(canvas: Canvas) {
        val w: Int = canvas.width
        val h: Int = canvas.height
        val plotW: Float = w - axisSpaceY
        val plotH: Float = h - axisSpaceX

        // Draw axis line
        canvas.drawLine(0f, plotH, w.toFloat(), plotH, axisPaint)

        // Draw scales
        for (i in scales.indices) {
            val scale: Float = scales[i]
            val alpha: Int = if ((i % 2 == 0)) scaleAlpha else 255
            textPaint.alpha = alpha
            linePaint.alpha = alpha
            val x: Float = plotW * (1 - (scale / this.scale))
            canvas.drawLine(x, 0f, x, plotH, linePaint)
            canvas.drawText("-$scale", x, h - textSpace, textPaint)
        }
    }

    fun scale(factor: Float) {
        scale /= factor
        scale = max(
            MIN_SCALE.toDouble(),
            min(scale.toDouble(), MAX_SCALE.toDouble())
        )
            .toFloat()
        updateScales()
    }

    private fun updateScales() {
        val powerNow: Double = getPowerOf2(scale, 0)
        val powerNext: Double = getPowerOf2(scale, 1)
        val powerHalf: Double = (powerNext + powerNow) / 2f
        val toHalf: Double = powerHalf - powerNow
        var n: Int = 4
        var max: Double = powerNext
        scaleAlpha = 255
        if (scale >= powerNow && scale < powerHalf) {
            scaleAlpha = (((powerHalf - scale) / toHalf) * 255).toInt()
            n = 6
            max = powerHalf
        }
        scales = ArrayList(n)
        val df: Float = (max / n).toFloat()
        var factor: Float = df
        while (factor < max) {
            scales.add(factor)
            factor += df
        }
    }

    private fun getPowerOf2(value: Float, next: Int): Double {
        val pow: Double = floor(ln(value.toDouble()) * LN2) + next
        return 2.0.pow(pow)
    }

    fun getPos(point: PlotData, w: Float): Float {
        return (w - axisSpaceY) * (1 - (point.secsToLatest() / scale))
    }

    companion object {
        private val MAX_SCALE: Float = 70f
        private val MIN_SCALE: Float = 1.1f
        private val LN2: Double = 1.4426950408889634
    }
}
