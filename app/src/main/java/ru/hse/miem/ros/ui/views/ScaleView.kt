package ru.hse.miem.ros.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import ru.hse.miem.ros.R
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 17.04.20
 * @updated on 17.04.20
 * @modified by
 */
class ScaleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    lateinit var smallLinePaint: Paint
    lateinit var bigLinePaint: Paint
    var scaleColor: Int = 0
    var bigLineWidth: Float = 0f
    var smallLineWidth: Float = 0f
    var middleH: Float = 0f
    var segments: Int = 0
    var segmentWidth: Float = 0f
    private var lineStart: Float = 0f
    private var lineEnd: Float = 0f
    private var firstQuarterHeight: Float = 0f
    private var thirdQuarterHeight: Float = 0f

    init {
        init()
    }

    private fun init() {
        bigLineWidth = Utils.dpToPx(context, 2f)
        smallLineWidth = Utils.dpToPx(context, 1f)
        bigLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bigLinePaint.color = ContextCompat.getColor(context, R.color.whiteMedium)
        bigLinePaint.strokeWidth = bigLineWidth
        smallLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        smallLinePaint.color = ContextCompat.getColor(context, R.color.whiteMedium)
        smallLinePaint.strokeWidth = smallLineWidth
        segments = 8
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        lineStart = bigLineWidth / 2
        lineEnd = w - bigLineWidth / 2
        middleH = h / 2f
        segmentWidth = (lineEnd - lineStart) / segments
        firstQuarterHeight = h / 4f
        thirdQuarterHeight = h / 4f * 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw horizontal line
        canvas.drawLine(lineStart, middleH, lineEnd, middleH, (smallLinePaint))

        // Draw vertical lines
        for (i in 0..segments) {
            var paint: Paint? = bigLinePaint
            val x: Float = lineStart + i * segmentWidth
            var startY = 0f
            var endY: Float = height.toFloat()
            if (i % (segments / 2) != 0) {
                startY = firstQuarterHeight
                endY = thirdQuarterHeight
                paint = smallLinePaint
            }
            canvas.drawLine(x, startY, x, endY, (paint)!!)
        }
    }
}
