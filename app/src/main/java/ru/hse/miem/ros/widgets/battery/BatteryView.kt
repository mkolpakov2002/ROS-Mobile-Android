package ru.hse.miem.ros.widgets.battery

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import org.ros.internal.message.Message
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import sensor_msgs.BatteryState
import kotlin.math.min

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 13.05.2021
 */
class BatteryView : SubscriberWidgetView {
    lateinit var outerPaint: Paint
    lateinit var innerPaint: Paint
    lateinit var textPaint: Paint
    var level: Int = 0
    var charging: Boolean = false
    var textSize: Float = 0f
    var borderWidth: Float = 0f
    lateinit var displayedText: String
    private var displayVoltage: Boolean = false
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            val newEntity: BatteryEntity = value as BatteryEntity
            displayVoltage = newEntity.displayVoltage
            if (displayVoltage) {
                updateVoltage(0f)
            } else {
                updatePercentage(0f)
            }
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        val textDip = 18f
        val borderDip = 10f
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, textDip,
            resources.displayMetrics
        )
        borderWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, borderDip,
            resources.displayMetrics
        )
        borderWidth = 10f
        level = 3

        // Init paints
        innerPaint = Paint()
        innerPaint.color = resources.getColor(R.color.battery5)
        innerPaint.strokeWidth = borderWidth
        innerPaint.strokeCap = Paint.Cap.ROUND
        outerPaint = Paint()
        outerPaint.color = resources.getColor(R.color.whiteHigh)
        outerPaint.style = Paint.Style.STROKE
        outerPaint.strokeWidth = borderWidth
        outerPaint.strokeCap = Paint.Cap.ROUND
        textPaint = Paint()
        textPaint.color = resources.getColor(R.color.whiteHigh)
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize
        updateColor()
    }

    public override fun onNewMessage(message: Message) {
        super.onNewMessage(message)
        val state: BatteryState = message as BatteryState
        charging = state.powerSupplyStatus == BatteryState.POWER_SUPPLY_STATUS_CHARGING
        if (displayVoltage) {
            updateVoltage(state.voltage)
        } else {
            updatePercentage(state.percentage)
        }
        this.invalidate()
    }

    private fun updatePercentage(value: Float) {
        val perc: Int = (value * 100).toInt()
        displayedText = "$perc%"
        level = min(5.0, (perc / 20 + 1).toDouble()).toInt()
        updateColor()
    }

    private fun updateVoltage(value: Float) {
        displayedText = if (value >= 10) {
            String.format("%.1fV", value)
        } else {
            String.format("%.2fV", value)
        }
        level = -1
        updateColor()
    }

    private fun updateColor() {
        val color: Int = when (level) {
            1 -> R.color.battery1
            2 -> R.color.battery2
            3 -> R.color.battery3
            4 -> R.color.battery4
            5 -> R.color.battery5
            else -> R.color.colorPrimary
        }
        innerPaint.color = resources.getColor(color)
    }

    @SuppressLint("DrawAllocation")
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        var middleX: Float = width / 2
        val left: Float = borderWidth / 2
        val right: Float = width - borderWidth / 2
        val top: Float = borderWidth * 2
        val bottom: Float = height - borderWidth - textSize

        // Draw pad
        canvas.drawRoundRect(
            middleX - width / 4, top - borderWidth,
            middleX + width / 4, top,
            borderWidth, borderWidth,
            (outerPaint)
        )

        // Draw body
        canvas.drawRoundRect(left, top, right, bottom, borderWidth, borderWidth, (outerPaint))
        if (charging) {
            // Draw lightning
            val batWidth: Float = right - left
            val batHeight: Float = bottom - top
            middleX = (right + left) / 2
            val middleY: Float = (bottom + top) / 2
            val partWidth: Float = batWidth / 5
            val partHeight: Float = batHeight / 6
            val path: Array<FloatArray> = arrayOf(
                floatArrayOf(middleX, middleY - partHeight),
                floatArrayOf(middleX - partWidth, middleY + partHeight / 5),
                floatArrayOf(middleX + partWidth, middleY - partHeight / 5),
                floatArrayOf(middleX, middleY + partHeight)
            )
            for (i in 0 until (path.size - 1)) {
                canvas.drawLine(
                    path[i][0],
                    path[i][1],
                    path[i + 1][0],
                    path[i + 1][1],
                    (innerPaint)
                )
            }
        } else {
            // Draw Bat level
            val batLevel: Int = if (level == -1) 5 else level
            val innerLeft: Float = left + borderWidth * 1.5f
            val innerRight: Float = right - borderWidth * 1.5f
            val innerTop: Float = top + borderWidth * 1.5f
            val innerBottom: Float = bottom - borderWidth * 1.5f
            val heightStep: Float =
                (innerBottom - innerTop + borderWidth) / MAX_LEVEL
            for (i in 0 until batLevel) {
                val b: Float = innerBottom - heightStep * i
                val t: Float = b - heightStep + borderWidth
                canvas.drawRect(innerLeft, t, innerRight, b, (innerPaint))
            }
        }

        // Draw status text
        canvas.drawText((displayedText), middleX, height, (textPaint))
    }

    companion object {
        val TAG: String = BatteryView::class.java.simpleName
        val MAX_LEVEL: Int = 5
    }
}
