package ru.hse.miem.ros.widgets.rqtplot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import org.ros.internal.message.Message
import org.ros.internal.message.field.Field
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import ru.hse.miem.ros.widgets.rqtplot.PlotDataList.PlotData
import std_msgs.Header
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 29.05.21
 */
class RqtPlotView : SubscriberWidgetView {
    lateinit var backgroundPaint: Paint
    lateinit var dataPaint: Paint
    lateinit var data: PlotDataList
    lateinit var scaleGestureDetector: ScaleGestureDetector
    lateinit var xAxis: XAxis
    lateinit var yAxis: YAxis
    lateinit var subPaths: ArrayList<String>
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            val entity: RqtPlotEntity = widgetEntity as RqtPlotEntity
            subPaths = ArrayList()
            for (subPath: String in entity.fieldPath.split("/".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()) {
                if (subPath.isNotEmpty()) {
                    subPaths.add(subPath.trim { it <= ' ' }.lowercase(Locale.getDefault()))
                }
            }
            yAxis.tickSteps = entity.height
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            return super.onTouchEvent(event)
        }
        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    private fun init() {
        val textSize: Float = 12 * resources.displayMetrics.density
        xAxis = XAxis(textSize)
        yAxis = YAxis(textSize)
        data = PlotDataList()
        data.maxTime = xAxis.scale * 1.5f
        scaleGestureDetector = ScaleGestureDetector(context, this@RqtPlotView.ScaleListener())
        backgroundPaint = Paint()
        backgroundPaint.color = Color.parseColor("#121212")
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE
        dataPaint = Paint()
        dataPaint.color = Color.YELLOW
        dataPaint.strokeWidth = 4f
        dataPaint.style = Paint.Style.STROKE
    }

    public override fun onNewMessage(message: Message) {
        var message: Message = message
        super.onNewMessage(message)

        // Try to extract value by traversing message path;
        try {
            val header: Header = message.toRawMessage().getMessage("header")
            for (i in subPaths.indices) {
                val path: String = subPaths[i]

                // Check if last subPath
                if (i == subPaths.size - 1) {
                    var value: Float? = null

                    // Find value and cast to float
                    for (field: Field in message.toRawMessage().fields) {
                        if ((field.name == path)) {
                            value = (field.getValue<Any>() as Number).toFloat()
                            break
                        }
                    }

                    // Add value to data
                    if (value != null) {
                        data.add(value.toDouble(), header.stamp)
                        yAxis.setLimits(data.minValue, data.maxValue)
                    } else {
                        Log.i(TAG, "Field couldnt be resolved. Unknown type.")
                    }
                } else {
                    message = message.toRawMessage().getMessage(path)
                }
            }
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.e(TAG, it) }
            return
        }
        this.invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w: Float = width.toFloat()
        val h: Float = height.toFloat()

        // Draw background
        canvas.drawRect(0f, 0f, w, h, (backgroundPaint))
        xAxis.draw(canvas)
        yAxis.draw(canvas)

        // Draw path
        if (data!!.size < 2) return
        for (i in 0 until (data.size - 1)) {
            val now: PlotData = data[i]
            val next: PlotData = data.get(i + 1)
            val xNow: Float = xAxis.getPos(now, w)
            val yNow: Float = yAxis.getPos(now, h)
            val xNext: Float = xAxis.getPos(next, w)
            val yNext: Float = yAxis.getPos(next, h)
            canvas.drawLine(xNow, yNow, xNext, yNext, (dataPaint))
        }
    }

    private inner class ScaleListener() : SimpleOnScaleGestureListener() {
        public override fun onScale(detector: ScaleGestureDetector): Boolean {
            xAxis.scale(detector.scaleFactor)
            data.maxTime = xAxis.scale * 1.5f
            invalidate()
            return true
        }
    }

    companion object {
        val TAG: String = RqtPlotView::class.java.simpleName
    }
}
