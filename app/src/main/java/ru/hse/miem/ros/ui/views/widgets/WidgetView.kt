package ru.hse.miem.ros.ui.views.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.ViewGroup
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.IPositionEntity
import ru.hse.miem.ros.ui.general.Position
import ru.hse.miem.ros.ui.general.WidgetEditListener
import kotlin.math.max

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
abstract class WidgetView : ViewGroup, IBaseView {
    var position: Position? = null
        protected set
    override var widgetEntity: BaseEntity? = null
        set(value) {
            field = value
            updatePosition()
        }
    private lateinit var editModeGestureDetector: GestureDetector
    private lateinit var editModeScaleGestureDetector: ScaleGestureDetector
    var editMode: Boolean = false
    protected var onWidgetEditListener: WidgetEditListener? = null
    private var tileWidth: Float = 0f
    private lateinit var highlightPaint: Paint
    private var shouldHighlight: Boolean = false

    constructor(context: Context?) : super(context) {
        baseInit()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        baseInit()
    }

    private fun baseInit() {
        setWillNotDraw(false)
        highlightPaint = Paint()
        highlightPaint.color = resources.getColor(R.color.colorPrimary)
        highlightPaint.style = Paint.Style.FILL_AND_STROKE
        highlightPaint.alpha = 100
        editModeGestureDetector = GestureDetector(context, EditModeOnGestureListener())
        editModeScaleGestureDetector =
            ScaleGestureDetector(context, EditModeOnScaleGestureListener())
    }

    private fun updatePosition() {
        position = (widgetEntity as IPositionEntity).getPosition()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {}
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (editMode && shouldHighlight) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), (highlightPaint))
        }
    }

    public override fun sameWidgetEntity(other: BaseEntity): Boolean {
        return other.id == (widgetEntity?.id ?: false)
    }

    fun setOnScaleListener(tileWidth: Float, onWidgetEditListener: (BaseEntity, Boolean) -> Unit) {
        this.tileWidth = tileWidth
        this.onWidgetEditListener =
            WidgetEditListener { entity, flag -> onWidgetEditListener(entity, flag) }
    }

    // GESTURES
    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            if (event.action == MotionEvent.ACTION_UP) {
                shouldHighlight = false
                invalidate()
            }
            editModeGestureDetector.onTouchEvent(event)
            editModeScaleGestureDetector.onTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }

    private inner class EditModeOnGestureListener() : SimpleOnGestureListener() {
        public override fun onDown(e: MotionEvent): Boolean {
            shouldHighlight = true
            invalidate()
            return true
        }

        public override fun onLongPress(e: MotionEvent) {
            val myShadow = DragShadowBuilder(this@WidgetView)
            startDrag(null, myShadow, this@WidgetView, 0)
            shouldHighlight = false
            invalidate()
        }
    }

    private inner class EditModeOnScaleGestureListener() : SimpleOnScaleGestureListener() {
        private var scaleFactorX: Float = 0f
        private var scaleFactorY: Float = 0f
        private fun setScaleFactor(detector: ScaleGestureDetector) {
            if (detector.previousSpanX > 0){
                scaleFactorX = scaleFactorX * detector.currentSpanX / detector.previousSpanX
                scaleFactorX = scaleFactorX * detector.currentSpanX / detector.previousSpanX
            }
        }

        private fun prepareScale(): BaseEntity {
            val posEntity: IPositionEntity = widgetEntity?.copy() as IPositionEntity
            val position: Position = posEntity.getPosition()
            position.width =
                max(1.0, Math.round((width * scaleFactorX) / tileWidth).toDouble())
                    .toInt()
            position.height =
                max(1.0, Math.round((height * scaleFactorY) / tileWidth).toDouble())
                    .toInt()
            posEntity.setPosition(position)
            return posEntity as BaseEntity
        }

        public override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            scaleFactorX = 1f
            scaleFactorY = 1f
            return true
        }

        public override fun onScale(detector: ScaleGestureDetector): Boolean {
            setScaleFactor(detector)
            onWidgetEditListener!!.onWidgetEdited(prepareScale(), false)
            return true
        }

        public override fun onScaleEnd(detector: ScaleGestureDetector) {
            setScaleFactor(detector)
            onWidgetEditListener!!.onWidgetEdited(prepareScale(), true)
        }
    }

    companion object {
        var TAG: String = WidgetView::class.java.simpleName
    }
}
