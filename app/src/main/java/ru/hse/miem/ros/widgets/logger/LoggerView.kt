package ru.hse.miem.ros.widgets.logger

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import org.ros.internal.message.Message

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 */
class LoggerView : SubscriberWidgetView {
    lateinit var data: String
    lateinit var textPaint: TextPaint
    lateinit var backgroundPaint: Paint
    lateinit var staticLayout: StaticLayout

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    public override fun onNewMessage(message: Message) {
        if (message !is std_msgs.String) return
        data = message.data
        this.invalidate()
    }

    private fun init() {
        data = "Logger"
        backgroundPaint = Paint()
        backgroundPaint.color = Color.BLACK
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint = TextPaint()
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = 20 * resources.displayMetrics.density
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        var textLayoutWidth: Float = width
        val entity: LoggerEntity = widgetEntity as LoggerEntity
        if (entity.rotation == 90 || entity.rotation == 270) {
            textLayoutWidth = height
        }
        canvas.drawRect(Rect(0, 0, width.toInt(), height.toInt()), (backgroundPaint))
        staticLayout = StaticLayout(
            data,
            textPaint, textLayoutWidth.toInt(),
            Layout.Alignment.ALIGN_CENTER,
            1.0f,
            0f,
            false
        )
        canvas.save()
        canvas.rotate(entity.rotation.toFloat(), width / 2, height / 2)
        canvas.translate(
            ((width / 2) - staticLayout.width / 2),
            height / 2 - staticLayout.height / 2
        )
        staticLayout.draw(canvas)
        canvas.restore()
    }

    companion object {
        private val TAG: String = LoggerView::class.java.simpleName
    }
}
