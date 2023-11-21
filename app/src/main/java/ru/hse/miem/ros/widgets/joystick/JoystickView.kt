package ru.hse.miem.ros.widgets.joystick

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.views.widgets.PublisherWidgetView
import ru.hse.miem.ros.utility.Utils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1.0
 * @created on 18.10.19
 */
class JoystickView : PublisherWidgetView {
    lateinit var outerPaint: Paint
    lateinit var linePaint: Paint
    lateinit var joystickPaint: Paint
    var joystickRadius: Float = 0f
    var posX: Float = 0f
    var posY: Float = 0f
    var rectangular: Boolean = false
    override var dataListener: DataListener? = null
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            val joy: JoystickEntity = value as JoystickEntity
            rectangular = joy.rectangularLimits
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        joystickRadius = Utils.cmToPx(context, 1f) / 2
        joystickPaint = Paint()
        joystickPaint.color = resources.getColor(R.color.colorAccent)
        outerPaint = Paint()
        outerPaint.color = resources.getColor(R.color.colorPrimary)
        outerPaint.style = Paint.Style.STROKE
        outerPaint.strokeWidth = Utils.dpToPx(context, 3f)
        linePaint = Paint()
        linePaint.color = resources.getColor(R.color.colorPrimary)
        linePaint.style = Paint.Style.STROKE
        linePaint.alpha = 50
        linePaint.strokeWidth = Utils.dpToPx(context, 2f)
    }

    // Move to polarCoordinates
    private fun moveTo(x: Float, y: Float) {
        posX = x
        posY = y
        publishViewData(JoystickData(posX, posY))

        // Redraw
        invalidate()
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            return super.onTouchEvent(event)
        }
        val eventX: Float = event.x
        val eventY: Float = event.y
        val polars: FloatArray = convertFromPxToRelative(eventX, eventY)
        when (event.actionMasked) {
            MotionEvent.ACTION_UP -> moveTo(0f, 0f)
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> moveTo(polars.get(0), polars.get(1))
            else -> return false
        }
        return true
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()
        val middleX: Float = width / 2
        val middleY: Float = height / 2
        val px: FloatArray = convertFromRelativeToPx(posX, posY)
        if (rectangular) {
            val rectW: Float = width - joystickRadius * 2
            val rectH: Float = height - joystickRadius * 2

            // Outer box
            canvas.drawRect(
                middleX - rectW / 2, middleY - rectH / 2,
                middleX + rectW / 2, middleY + rectH / 2, (outerPaint)
            )

            // Inner box
            canvas.drawRect(
                middleX - rectW / 4, middleY - rectH / 4,
                middleX + rectW / 4, middleY + rectH / 4, (linePaint)
            )
            canvas.drawLine(
                middleX, joystickRadius,
                middleX, joystickRadius + rectH, (linePaint)
            )
            canvas.drawLine(
                joystickRadius, middleY,
                joystickRadius + rectW, middleY, (linePaint)
            )
        } else {
            // Outer ring
            canvas.drawCircle(middleX, middleY, middleX - joystickRadius, (outerPaint))

            // Inner ring
            canvas.drawCircle(middleX, middleY, (middleX - joystickRadius) / 2, (linePaint))
            canvas.drawLine(
                middleX, middleY - middleX + joystickRadius,
                middleX, middleY + middleX - joystickRadius, (linePaint)
            )
            canvas.drawLine(
                joystickRadius, middleY,
                width - joystickRadius, middleY, (linePaint)
            )
        }

        // Stick
        canvas.drawCircle(px.get(0), px.get(1), joystickRadius, (joystickPaint))

        /*
        float width = getWidth();
        float height = getHeight();

        float[] px = convertFromPolarToPx(posX, posY);

        JoystickEntity entity = (JoystickEntity) widgetEntity;

        if(entity.rectangularLimits){
            // Outer box
            canvas.drawRect(joystickRadius, joystickRadius, width-joystickRadius, height-joystickRadius, outerPaint);
            // Inner box
            canvas.drawRect(width/4 + joystickRadius/2, height/4+joystickRadius/2, width*(3f/4)-joystickRadius/2, height*(3f/4)-joystickRadius/2, linePaint);

        } else {
            // Outer ring
            canvas.drawCircle(width/2, height/2, width/2- joystickRadius, outerPaint);
            // Inner drawings
            canvas.drawCircle(width/2, height/2, width/4 - joystickRadius/2, linePaint);
        }

        canvas.drawLine(joystickRadius, height/2, width-joystickRadius, height/2,  linePaint);
        canvas.drawLine(width/2, height/2 - width/2 + joystickRadius ,
                        width/2, height/2 + width/2 - joystickRadius,  linePaint);

        // Stick
        canvas.drawCircle(px[0], px[1], joystickRadius, joystickPaint);
        */
    }

    private fun convertFromPxToRelative(x: Float, y: Float): FloatArray {
        val middleX: Float = width / 2f
        val middleY: Float = height / 2f
        val relPos: FloatArray = FloatArray(2)
        val dx: Float = x - middleX
        val dy: Float = y - middleY
        if (rectangular) {
            val maxW: Float = middleX - joystickRadius
            val maxH: Float = middleY - joystickRadius
            relPos[0] = min(1.0, max(-1.0, (dx / maxW).toDouble())).toFloat()
            relPos[1] = min(1.0, max(-1.0, (-dy / maxH).toDouble())).toFloat()
        } else {
            val r: Float = middleX - joystickRadius
            val rad: Double = atan2(dy.toDouble(), dx.toDouble())
            var len: Double = sqrt((dx * dx + dy * dy).toDouble()) / r
            len = min(1.0, len)
            relPos[0] = (cos(rad) * len).toFloat()
            relPos[1] = (-sin(rad) * len).toFloat()
        }
        return relPos
    }

    private fun convertFromRelativeToPx(x: Float, y: Float): FloatArray {
        val middleX: Float = width / 2f
        val middleY: Float = height / 2f
        val px: FloatArray = FloatArray(2)
        if (rectangular) {
            val maxW: Float = middleX - joystickRadius
            val maxH: Float = middleY - joystickRadius
            px[0] = middleX + x * maxW
            px[1] = middleY - y * maxH
        } else {
            val r: Float = middleX - joystickRadius
            px[0] = middleX + x * r
            px[1] = middleY - y * r
        }
        return px
    }

    companion object {
        val TAG: String = JoystickView::class.java.simpleName
    }
}
