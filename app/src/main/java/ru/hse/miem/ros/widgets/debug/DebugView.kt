package ru.hse.miem.ros.widgets.debug

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import org.ros.internal.message.Message
import kotlin.math.max

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 17.08.20
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 */
class DebugView : SubscriberWidgetView {
    private val dragSensitivity: Float = 1f //0.05f;

    // Canvas parameter
    private lateinit var paint: Paint
    private lateinit var paintDark: Paint
    private var cornerWidth: Float = 0f

    // GestureDetector for doubleClick
    private lateinit var gestureDetector: GestureDetectorCompat

    // Views
    private lateinit var scrollView: ScrollView
    private lateinit var textView: TextView

    // Container for textView output
    private var stopUpdate: Boolean = false
    private lateinit var output: String
    private lateinit var dataList: ArrayList<String?>

    // Finger position tracker
    private var lastY: Float = 0.0f
    private var mode: Int = 0

    // Amount of translation
    private var translateY: Float = 0f

    // Drag parameters
    private var posY: Int = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        // Set canvas parameter
        cornerWidth = 0f //Utils.dpToPx(getContext(), 8);
        paint = Paint()
        paint.color = resources.getColor(R.color.borderColor)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.textSize = 20f

        // Initalize Views
        scrollView = ScrollView(context)
        textView = TextView(context)
        textView.visibility = VISIBLE

        // Define action for onDoubleTap
        gestureDetector = GestureDetectorCompat(
            context,
            object : SimpleOnGestureListener() {
                public override fun onDoubleTap(e: MotionEvent): Boolean {
                    stopUpdate = !stopUpdate
                    if ((stopUpdate)) {
                        output = ""
                        for (string: String? in dataList) {
                            output += string
                            output += "\n\n"
                        }
                        updateView()
                    }
                    return true
                }
            })

        // Initialize variables
        stopUpdate = false
        output = ""
        dataList = ArrayList()

        // Background color
        paintDark = Paint()
        paintDark.color = Color.argb(100, 0, 0, 0)
        //paintDark.setColor(getResources().getColor(R.color.black02dp));
        paintDark.style = Paint.Style.FILL
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        // Get vizualization size
        val leftViz: Float = 0f
        val topViz: Float = 0f
        val widthViz: Float = width.toFloat()
        val heightViz: Float = height.toFloat()

        // Draw background and rectangle
        canvas.drawPaint((paintDark))
        canvas.drawRoundRect(
            leftViz,
            topViz,
            widthViz,
            heightViz,
            cornerWidth,
            cornerWidth,
            (paint)
        )
        canvas.translate(cornerWidth, cornerWidth)

        // Calculate the drag
        //posY = posY - (int) (translateY * dragSensitivity);
        //posY = Math.max(posY, 0);

        // Draw data
        textView.scrollTo(0, posY)
        scrollView.measure(width, height)
        scrollView.layout(0, 0, width, height)
        scrollView.draw(canvas)

        // Apply changes
        canvas.restore()
    }

    public override fun onNewMessage(message: Message) {
        val debugData = DebugData(message)
        val entity: DebugEntity = widgetEntity as DebugEntity
        dataList.add(debugData.value)
        while (dataList.size > entity.numberMessages) {
            dataList.removeAt(0)
        }
        if (!stopUpdate) {
            output = debugData.value
            updateView()
        }
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            return super.onTouchEvent(event)
        }

        // Handle double click
        gestureDetector.onTouchEvent(event)

        // Handle scrolling
        if ((stopUpdate)) {
            var dragged = false
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mode = DRAG
                    lastY = event.y
                }

                MotionEvent.ACTION_MOVE -> {
                    translateY = event.y - lastY
                    lastY = event.y
                    posY -= (translateY * dragSensitivity).toInt()
                    posY = max(posY.toDouble(), 0.0).toInt()
                    if (translateY != 0f) {
                        dragged = true
                    }
                }

                MotionEvent.ACTION_UP -> {
                    mode = NONE
                    dragged = false
                }

                MotionEvent.ACTION_POINTER_UP -> mode = DRAG
            }
            if ((mode == DRAG && dragged)) {
                this.invalidate()
            }
        }
        return true
    }

    private fun updateView() {
        val width: Int = width - (cornerWidth * 2).toInt()
        textView.text = output
        textView.measure(width, 0)
        scrollView.removeView(textView)
        scrollView.addView(textView, width, textView.measuredHeight)
        this.invalidate()
    }

    companion object {
        val TAG: String = DebugView::class.java.simpleName

        // Mode
        private val NONE: Int = 0
        private val DRAG: Int = 1
    }
}