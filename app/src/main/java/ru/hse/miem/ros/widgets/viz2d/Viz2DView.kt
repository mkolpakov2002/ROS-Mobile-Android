package ru.hse.miem.ros.widgets.viz2d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.opengl.visualisation.VisualizationView
import ru.hse.miem.ros.ui.views.widgets.LayerView
import ru.hse.miem.ros.ui.views.widgets.PublisherLayerView
import ru.hse.miem.ros.ui.views.widgets.WidgetGroupView

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class Viz2DView : WidgetGroupView {
    private val border: Int = 4
    override var dataListener: DataListener? = null
    private lateinit var paintBackground: Paint
    private lateinit var layerView: VisualizationView
    override var widgetEntity: BaseEntity? = null
        get() = super.widgetEntity
        set(value) {
            field = value
            layerView.camera.jumpToFrame((value as Viz2DEntity).frame)
        }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        layerView.layout(border, border, width - border, height - border)
    }

    private fun init() {
        // Border color painted as Background
        val borderColor: Int = context.resources.getColor(R.color.borderColor)
        paintBackground = Paint()
        paintBackground.color = borderColor
        paintBackground.style = Paint.Style.FILL
        layerView = VisualizationView(context)
        this.addView(layerView)
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        return layerView.onTouchEvent(event)
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawPaint((paintBackground))
        super.onDraw(canvas)
    }

    public override fun onNewData(data: RosData) {
        layerView.onNewData(data)
    }

    public override fun publishViewData(data: BaseData) {
        if (dataListener == null) return
        dataListener?.let {
            data.topic = (widgetEntity?.topic)
            it.onNewWidgetData(data)
        }
    }

    public override fun addLayer(layer: LayerView) {
        if (layer is PublisherLayerView) {
            layer.dataListener = (DataListener { data: BaseData ->
                dataListener?.onNewWidgetData(data)
            })
        }
        layerView.addLayer(layer)
    }

    companion object {
        val TAG: String = Viz2DView::class.java.simpleName
    }
}