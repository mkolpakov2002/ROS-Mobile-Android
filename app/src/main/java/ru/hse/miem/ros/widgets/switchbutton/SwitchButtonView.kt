package ru.hse.miem.ros.widgets.switchbutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.views.widgets.PublisherWidgetView
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.05.2022
 */
class SwitchButtonView : PublisherWidgetView {
    private lateinit var switchOnPaint: Paint
    private lateinit var switchOffPaint: Paint
    private lateinit var innerSwitchOnPaint: Paint
    private lateinit var innerSwitchOffPaint: Paint
    private lateinit var thumbSwitchOnPaint: Paint
    private lateinit var thumbSwitchOffPaint: Paint
    private lateinit var textPaintOn: TextPaint
    private lateinit var textPaintOff: TextPaint
    private lateinit var staticLayout: StaticLayout
    private var switchState: Boolean = false
    override var dataListener: DataListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        switchOnPaint = Paint()
        switchOnPaint.color = resources.getColor(R.color.colorPrimary)
        switchOnPaint.style = Paint.Style.FILL_AND_STROKE
        switchOffPaint = Paint()
        switchOffPaint.color = resources.getColor(R.color.colorPrimary)
        switchOffPaint.style = Paint.Style.STROKE
        switchOffPaint.strokeWidth = Utils.dpToPx(context, 3f)
        textPaintOn = TextPaint()
        textPaintOn.color = resources.getColor(R.color.colorPrimaryDark)
        textPaintOn.textSize = 26 * resources.displayMetrics.density
        textPaintOn.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        textPaintOff = TextPaint()
        textPaintOff.color = resources.getColor(R.color.colorPrimary)
        textPaintOff.textSize = 26 * resources.displayMetrics.density
        textPaintOff.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        thumbSwitchOnPaint = Paint()
        thumbSwitchOnPaint.color = resources.getColor(R.color.battery1)
        thumbSwitchOnPaint.style = Paint.Style.FILL
        innerSwitchOnPaint = Paint()
        innerSwitchOnPaint.color = resources.getColor(R.color.battery2)
        innerSwitchOnPaint.style = Paint.Style.FILL
        thumbSwitchOffPaint = Paint()
        thumbSwitchOffPaint.color = resources.getColor(R.color.battery3)
        thumbSwitchOffPaint.style = Paint.Style.FILL
        innerSwitchOffPaint = Paint()
        innerSwitchOffPaint.color = resources.getColor(R.color.battery5)
        innerSwitchOffPaint.style = Paint.Style.FILL
    }

    private fun changeState() {
        switchState = !switchState
        publishViewData(SwitchButtonData(switchState))
        invalidate()
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.e(TAG, event.toString())
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            val eventX: Float = event.x
            val eventY: Float = event.y
            val width: Float = width.toFloat()
            val height: Float = height.toFloat()
            if ((eventX > 0) && (eventX < width) && (eventY > 0) && (eventY < height)) changeState()
        }
        return true
    }

    public override fun onDraw(canvas: Canvas) {
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        val innerSwitchPaint: Paint = if (switchState) switchOnPaint else switchOffPaint
        canvas.drawRoundRect(5f, 5f, width - 10, height - 10, 10f, 10f, (innerSwitchPaint))
        val entity: SwitchButtonEntity? = widgetEntity as SwitchButtonEntity?
        val entityText: String = entity?.text ?: "Switch"
        val textPaint: TextPaint = if (switchState) textPaintOn else textPaintOff
        staticLayout = StaticLayout(
            entityText, textPaint, width.toInt(), Layout.Alignment.ALIGN_CENTER,
            1.0f, 0f, false
        )
        canvas.save()
        canvas.translate(
            ((width - staticLayout.width) / 2f),
            (height - staticLayout.height) / 2f
        )
        staticLayout.draw(canvas)
        canvas.restore()
    }

    companion object {
        val TAG: String = SwitchButtonView::class.java.simpleName
    }
}
