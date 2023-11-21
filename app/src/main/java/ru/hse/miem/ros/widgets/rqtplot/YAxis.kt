package ru.hse.miem.ros.widgets.rqtplot

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import ru.hse.miem.ros.widgets.rqtplot.PlotDataList.PlotData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.pow

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 03.06.21
 */
class YAxis(textSize: Float) {
    var textPaint: TextPaint
    var axisPaint: Paint = Paint()
    var linePaint: Paint
    lateinit var scales: MutableList<Float>
    var textSpace: Float
    var textWidth: Float
    var textHeight: Float
    var axisSpaceX: Float
    var axisSpaceY: Float
    var limitMin: Double = 0.0
    var limitMax: Double = 0.0
    var lowerBound: Float = 0f
    var upperBound: Float = 0f
    var hasLimits: Boolean
    var tickSteps: Int = 8
        set(value) {
            field = max(2.0, value.toDouble()).toInt()
        }

    init {
        axisPaint.color = Color.WHITE
        axisPaint.strokeWidth = 3f
        axisPaint.style = Paint.Style.STROKE
        textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.LEFT
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
        hasLimits = false
        updateScales()
    }

    fun draw(canvas: Canvas) {
        val w: Int = canvas.width
        val h: Int = canvas.height
        val plotW: Float = w - axisSpaceY
        val plotH: Float = h - axisSpaceX

        // Draw axis line
        canvas.drawLine(plotW, 0f, plotW, h.toFloat(), axisPaint)
        val textX: Float = plotW + 10
        var minValue: Double = lowerBound.toDouble()
        var maxValue: Double = upperBound.toDouble()
        if (!hasLimits) {
            minValue = 0.0
            maxValue = 1.0
        }
        val maxScale: Double = maxValue - minValue

        // Draw scales
        for (i in scales.indices) {
            val scale: Float = scales[i]
            val y: Float = ((plotH - 80) * (1 - ((scale - minValue) / maxScale)) + 40).toFloat()
            canvas.drawLine(0f, y, plotW, y, linePaint)
            val scaleText: String = if (abs(scale.toDouble()) <= 1) {
                String.format("%.2f", scale)
            } else if (abs(scale.toDouble()) <= 10) {
                String.format("%.1f", scale)
            } else {
                String.format("%d", scale.toInt())
            }
            canvas.drawText(scaleText, textX, y + textHeight / 2, textPaint)
        }
    }

    private fun updateScales() {
        var min: Float = limitMin.toFloat()
        var max: Float = limitMax.toFloat()
        if (!hasLimits) {
            min = 0f
            max = 1f
        }
        val range: Float = max - min
        val roughStep: Double = (range / (tickSteps - 1)).toDouble()
        //double[] normalizedSteps = {1, 1.5, 2, 2.5, 5, 7.5, 10}; // keep the 10 at the end
        val normalizedSteps: DoubleArray = doubleArrayOf(1.0, 2.0, 5.0, 10.0)
        val powX: Double = 10.0.pow(-floor(log10(abs(roughStep))))
        val normalizedStep: Double = roughStep * powX
        var goodPowX = 0.0
        for (n: Double in normalizedSteps) {
            if (n >= normalizedStep) {
                goodPowX = n
                break
            }
        }
        val dist: Double = goodPowX / powX

        // Determine the scale limits based on the chosen step.
        upperBound = (ceil(max / dist) * dist).toFloat()
        lowerBound = (floor(min / dist) * dist).toFloat()
        scales = ArrayList(tickSteps + 1)
        var factor: Double = lowerBound.toDouble()
        while (upperBound - factor > -0.000001) {
            scales.add(factor.toFloat())
            factor += dist
        }
    }

    fun getPos(point: PlotData, h: Float): Float {
        return (((h - axisSpaceX) - 80)
                * (1 - ((point.value - lowerBound) / (upperBound - lowerBound))) + 40).toFloat()
    }

    fun setLimits(minValue: Double, maxValue: Double) {
        limitMin = minValue
        limitMax = maxValue
        hasLimits = maxValue - minValue > 0.001
        updateScales()
    }

    companion object {
        private val TAG: String = YAxis::class.java.simpleName
    }
}
