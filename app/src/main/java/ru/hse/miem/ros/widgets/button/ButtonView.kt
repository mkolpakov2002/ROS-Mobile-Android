package ru.hse.miem.ros.widgets.button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.views.widgets.PublisherWidgetView

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 * @updated on 10.03.2021
 * @modified by Maxim Kolpakov
 */
class ButtonView : PublisherWidgetView {
    lateinit var buttonPaint: Paint
    lateinit var textPaint: TextPaint
    lateinit var staticLayout: StaticLayout
    override var dataListener: DataListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        buttonPaint = Paint()
        buttonPaint.color = resources.getColor(R.color.colorPrimary)
        buttonPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint = TextPaint()
        textPaint.color = Color.BLACK
        textPaint.style = Paint.Style.FILL_AND_STROKE
        textPaint.textSize = 26 * resources.displayMetrics.density
    }

    private fun changeState(pressed: Boolean) {
        publishViewData(ButtonData(pressed))
        invalidate()
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            return super.onTouchEvent(event)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> {
                buttonPaint.color = resources.getColor(R.color.colorPrimary)
                changeState(false)
            }

            MotionEvent.ACTION_DOWN -> {
                buttonPaint.color = resources.getColor(R.color.color_attention)
                changeState(true)
            }

            else -> return false
        }
        return true
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        var textLayoutWidth: Float = width
        val entity: ButtonEntity = widgetEntity as ButtonEntity
        if (entity.rotation == 90 || entity.rotation == 270) {
            textLayoutWidth = height
        }
        canvas.drawRect(Rect(0, 0, width.toInt(), height.toInt()), (buttonPaint))
        staticLayout = StaticLayout(
            entity.text,
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
        val TAG: String = ButtonView::class.java.simpleName
    }
}
