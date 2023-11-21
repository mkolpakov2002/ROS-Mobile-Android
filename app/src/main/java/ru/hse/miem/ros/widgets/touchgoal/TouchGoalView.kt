package ru.hse.miem.ros.widgets.touchgoal

import android.content.Context
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.opengl.visualisation.OpenGlTransform
import ru.hse.miem.ros.ui.opengl.visualisation.ROSColor
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.PublisherLayerView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.05.2021
 */
class TouchGoalView(context: Context) : PublisherLayerView(context) {
    private val detector: GestureDetectorCompat
    private val numPoints: Int = 51
    private val color: ROSColor
    private var state: State = State.NORMAL
    private lateinit var data: TouchGoalData
    private lateinit var circleBuffer: FloatBuffer
    private lateinit var lineBuffer: FloatBuffer
    override var dataListener: DataListener? = null

    init {
        detector = GestureDetectorCompat(context, DoubleTapListener())
        initCircle()
        color = ROSColor.fromInt(context.resources.getColor(R.color.colorPrimary))
    }

    private fun initCircle() {
        val pufferBuffer: ByteBuffer =
            ByteBuffer.allocateDirect(numPoints * 3 * java.lang.Float.SIZE)
        pufferBuffer.order(ByteOrder.nativeOrder())
        circleBuffer = pufferBuffer.asFloatBuffer()
        val angDiff: Float = (Math.PI * 2 / (numPoints - 1)).toFloat()
        for (i in 0 until numPoints) {
            val angle: Float = angDiff * i
            circleBuffer.put(cos(angle.toDouble()).toFloat())
            circleBuffer.put(sin(angle.toDouble()).toFloat())
            circleBuffer.put(0f)
        }
        circleBuffer.position(0)
    }

    private fun initLine() {
        val pufferBuffer: ByteBuffer = ByteBuffer.allocateDirect(2 * 3 * java.lang.Float.SIZE)
        pufferBuffer.order(ByteOrder.nativeOrder())
        lineBuffer = pufferBuffer.asFloatBuffer()
        data.start?.translation?.x?.let { lineBuffer.put(it.toFloat()) }
        data.start?.translation?.y?.let { lineBuffer.put(it.toFloat()) }
        lineBuffer.put(0f)
        data.end?.translation?.x?.let { lineBuffer.put(it.toFloat()) }
        data.end?.translation?.y?.let { lineBuffer.put(it.toFloat()) }
        lineBuffer.put(0f)
        lineBuffer.position(0)
    }

    public override fun draw(view: VisualizationView, gl: GL10) {
        if (state != State.DOUBLETAPPED) return
        gl.glPushMatrix()
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        color.apply(gl)
        gl.glLineWidth(10f)

        // Draw line from goal start to end
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineBuffer)
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, 2)

        // Adjust frame
        OpenGlTransform.apply(gl, data.start)
        gl.glScalef(50f, 50f, 1f)
        // Counter adjust for the camera zoom.
        val counterZoom: Float = 1 / view.camera.zoom.toFloat()
        gl.glScalef(counterZoom, counterZoom, 1f)

        // Draw circle around goal start
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, circleBuffer)
        gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, numPoints)
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
        gl.glPopMatrix()
    }

    public override fun onTouchEvent(
        visualizationView: VisualizationView?,
        event: MotionEvent
    ): Boolean {
        detector.onTouchEvent(event)
        if (event.action == MotionEvent.ACTION_MOVE) {
            if (state == State.DOUBLETAPPED) {
                data.setEnd(event.x, event.y)
                initLine()
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (state == State.DOUBLETAPPED) {
                if (data.isValid) publishViewData(data)
            }
            state = State.NORMAL
        }
        return state == State.DOUBLETAPPED
    }

    private enum class State {
        NORMAL,
        DOUBLETAPPED
    }

    internal inner class DoubleTapListener() : SimpleOnGestureListener() {
        public override fun onDoubleTap(e: MotionEvent): Boolean {
            state = State.DOUBLETAPPED
            data = TouchGoalData(parentView.camera)
            data.setStart(e.x, e.y)
            data.setEnd(e.x, e.y)
            initLine()
            return true
        }
    }

    companion object {
        val TAG: String = TouchGoalView::class.java.simpleName
    }
}