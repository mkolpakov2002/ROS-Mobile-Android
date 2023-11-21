package ru.hse.miem.ros.widgets.camera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import org.ros.internal.message.Message
import sensor_msgs.CompressedImage
import sensor_msgs.Image

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.1
 * @created on 27.04.19
 * @updated on 20.10.2020
 * @modified by Maxim Kolpakov
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 */
class CameraView : SubscriberWidgetView {
    private val imageRect: RectF = RectF()
    private lateinit var borderPaint: Paint
    private lateinit var paintBackground: Paint
    private var cornerWidth: Float = 0f
    private var data: CameraData? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        cornerWidth = 0f //Utils.dpToPx(getContext(), 8);
        borderPaint = Paint()
        borderPaint.color = resources.getColor(R.color.borderColor)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 8f

        // Background color
        paintBackground = Paint()
        paintBackground.color = Color.argb(100, 0, 0, 0)
        paintBackground.style = Paint.Style.FILL
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPaint((paintBackground))

        // Define image size based on the Bitmap width and height
        val leftViz = 0f
        val topViz = 0f
        val widthViz = width.toFloat()
        val heightViz = height.toFloat()
        var width = widthViz
        var height: Float = heightViz
        var left: Float = leftViz
        var top: Float = topViz
        data?.let{
            val mapRatio: Float = it.map!!.height.toFloat() / it.map!!.width
            val vizRatio: Float = heightViz / widthViz
            if (mapRatio >= vizRatio) {
                height = heightViz
                width = (vizRatio / mapRatio) * widthViz
                left = 0.5f * (widthViz - width)
            } else if (vizRatio > mapRatio) {
                width = widthViz
                height = (mapRatio / vizRatio) * heightViz
                top = 0.5f * (heightViz - height)
            }
            imageRect.set(left, top, left + width, top + height)
            canvas.drawBitmap((it.map)!!, null, imageRect, borderPaint)
        }

        // Draw Border
        canvas.drawRoundRect(
            leftViz,
            topViz,
            widthViz,
            heightViz,
            cornerWidth,
            cornerWidth,
            (borderPaint)
        )
    }

    public override fun onNewMessage(message: Message) {
        data = null
        data = when (message) {
            is CompressedImage -> {
                CameraData(message)
            }

            is Image -> {
                CameraData(message)
            }

            else -> {
                return
            }
        }
        this.invalidate()
    }

    companion object {
        val TAG: String = CameraView::class.java.simpleName
    }
}