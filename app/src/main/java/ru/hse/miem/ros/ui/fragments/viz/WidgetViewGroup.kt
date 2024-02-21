package ru.hse.miem.ros.ui.fragments.viz

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import org.ros.internal.message.Message
import ru.hse.miem.ros.BuildConfig
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.data.model.entities.widgets.IPositionEntity
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.RosData
import ru.hse.miem.ros.data.model.repositories.rosRepo.message.Topic
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.AbstractNode
import ru.hse.miem.ros.data.model.repositories.rosRepo.node.BaseData
import ru.hse.miem.ros.ui.general.DataListener
import ru.hse.miem.ros.ui.general.Position
import ru.hse.miem.ros.ui.general.WidgetChangeListener
import ru.hse.miem.ros.ui.views.widgets.IBaseView
import ru.hse.miem.ros.ui.views.widgets.IPublisherView
import ru.hse.miem.ros.ui.views.widgets.ISubscriberView
import ru.hse.miem.ros.ui.views.widgets.LayerView
import ru.hse.miem.ros.ui.views.widgets.WidgetGroupView
import ru.hse.miem.ros.ui.views.widgets.WidgetView
import ru.hse.miem.ros.utility.Constants
import ru.hse.miem.ros.utility.Utils
import java.lang.reflect.Constructor
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1.2
 * @created on 18.10.19
 * @updated on 22.04.20
 * @modified by Tanya Rykova
 * @updated on 25.09.20
 * @modified by Tanya Rykova
 */
class WidgetViewGroup(context: Context?, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    private var crossPaint: Paint
    private var scaleShadowPaint: Paint
    private var tilesX: Int = 0
    private var tilesY: Int = 0
    private var tileWidth: Float = 0f
    private var widgetList: MutableList<BaseEntity?> = ArrayList()
    private var dataListener: DataListener? = null
    private var widgetDetailsChangedListener: WidgetChangeListener? = null
    private var vizEditMode: Boolean = false
    private var drawWidgetScaleShadow: Boolean = false
    private lateinit var widgetScaleShadowPosition: Position

    init {
        val a: TypedArray = getContext().obtainStyledAttributes(
            attrs,
            R.styleable.WidgetViewGroup, 0, 0
        )
        val crossColor: Int = a.getColor(
            R.styleable.WidgetViewGroup_crossColor,
            resources.getColor(R.color.colorAccent)
        )
        a.recycle()
        val stroke: Float = Utils.dpToPx(getContext(), 1f)
        crossPaint = Paint()
        crossPaint.color = crossColor
        crossPaint.strokeWidth = stroke
        scaleShadowPaint = Paint()
        scaleShadowPaint.color = resources.getColor(R.color.colorPrimary)
        scaleShadowPaint.style = Paint.Style.FILL_AND_STROKE
        scaleShadowPaint.alpha = 100
        setWillNotDraw(false)
        setOnDragListener { view: View?, event: DragEvent ->
            if (event.action == DragEvent.ACTION_DROP && vizEditMode) {
                val widget: WidgetView = event.localState as WidgetView
                val entity: IPositionEntity? = widget.widgetEntity?.copy() as IPositionEntity?
                val position: Position = entity!!.getPosition()
                position.x = Math.round((event.x - widget.width / 2f) / tileWidth)
                position.y =
                    tilesY - Math.round((event.y + widget.height / 2f) / tileWidth)
                entity.setPosition(position)
                widgetDetailsChangedListener!!.onWidgetDetailsChanged(entity as BaseEntity)
            }
            true
        }
    }

    private fun calculateTiles() {
        val width: Float = (width - paddingLeft - paddingRight).toFloat()
        val height: Float = (height - paddingBottom - paddingTop).toFloat()
        if (width < height) { // Portrait
            tilesX = TILES_X
            tileWidth = width / tilesX
            tilesY = (height / tileWidth).toInt()
        } else { // Landscape
            tilesY = TILES_X
            tileWidth = height / tilesY
            tilesX = (width / tileWidth).toInt()
        }
    }

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        var widthMeasureSpec: Int = widthMeasureSpec
        var heightMeasureSpec: Int = heightMeasureSpec
        val width: Int = MeasureSpec.getSize(widthMeasureSpec)
        val height: Int = MeasureSpec.getSize(heightMeasureSpec)
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        val count: Int = childCount
        for (i in 0 until count) {
            val v: View = getChildAt(i)
            // this works because you set the dimensions of the ImageView to FILL_PARENT
            v.measure(
                MeasureSpec.makeMeasureSpec(
                    measuredWidth,
                    MeasureSpec.EXACTLY
                ), MeasureSpec.makeMeasureSpec(
                    measuredHeight, MeasureSpec.EXACTLY
                )
            )
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * Position all children within this layout.
     */
    public override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        calculateTiles()
        for (i in 0 until childCount) {
            positionChild(i)
        }
    }

    private fun positionChild(i: Int) {
        val child: View = getChildAt(i)

        // Check if view is visible
        if (child.visibility == GONE) return
        val position: Position = (child as WidgetView).position

        // Y pos from bottom up
        val w: Int = (position.width * tileWidth).toInt()
        val h: Int = (position.height * tileWidth).toInt()
        val x: Int = (paddingLeft + position.x * tileWidth).toInt()
        val y: Int =
            (paddingTop + (tilesY - (position.height + position.y)) * tileWidth).toInt()

        // Place the child.
        child.layout(x, y, x + w, y + h)
    }

    public override fun onDraw(canvas: Canvas) {
        val startX: Float = paddingLeft.toFloat()
        val endX: Float = (width - paddingRight).toFloat()
        val startY: Float = paddingTop.toFloat()
        val endY: Float = (height - paddingBottom).toFloat()

        // Draw x's
        val lineLen: Float = Utils.dpToPx(context, 5f) / 2
        var drawY: Float = startY
        while (drawY <= endY) {
            var drawX: Float = startX
            while (drawX <= endX) {
                canvas.drawLine(drawX - lineLen, drawY, drawX + lineLen, drawY, crossPaint)
                canvas.drawLine(drawX, drawY - lineLen, drawX, drawY + lineLen, crossPaint)
                drawX += tileWidth
            }
            drawY += tileWidth
        }
        if (drawWidgetScaleShadow && this::widgetScaleShadowPosition.isInitialized) {
            val w: Int = (widgetScaleShadowPosition.width * tileWidth).toInt()
            val h: Int = (widgetScaleShadowPosition.height * tileWidth).toInt()
            val x: Int = (paddingLeft + widgetScaleShadowPosition.x * tileWidth).toInt()
            val y: Int =
                (paddingTop + (tilesY - (widgetScaleShadowPosition.height + widgetScaleShadowPosition.y)) * tileWidth).toInt()
            canvas.drawRect(
                x.toFloat(),
                y.toFloat(),
                (x + w).toFloat(),
                (y + h).toFloat(),
                scaleShadowPaint
            )
        }
    }

    private fun informDataChange(data: BaseData) {
        dataListener?.onNewWidgetData(data)
    }

    fun onNewData(data: RosData) {
        val message: Message = data.message
        val topic: Topic = data.topic
        for (i in 0 until childCount) {
            val view: View = getChildAt(i)
            if (view !is ISubscriberView) continue
            if (view is WidgetGroupView) {
                view.onNewData(data)
            } else {
                val baseView: IBaseView = view as IBaseView
                if ((((baseView.widgetEntity?.topic ?: false) == topic))) {
                    (view as ISubscriberView).onNewMessage(message)
                }
            }
        }
    }

    fun setWidgets(newWidgets: List<BaseEntity?>?, map: HashMap<Topic?, AbstractNode>) {
        var changes: Boolean = false

        // Create widget check with ids
        val widgetCheckMap: HashMap<Long, Boolean> = HashMap()
        val widgetEntryMap: HashMap<Long, BaseEntity?> = HashMap()
        for (oldWidget: BaseEntity? in widgetList) {
            widgetCheckMap[oldWidget!!.id] = false
            widgetEntryMap[oldWidget.id] = oldWidget
        }
        for (newWidget: BaseEntity? in newWidgets!!) {
            if (widgetCheckMap.containsKey(newWidget!!.id)) {
                widgetCheckMap[newWidget.id] = true

                // Check if widget has changed
                val oldWidget: BaseEntity? = widgetEntryMap[newWidget.id]
                if (oldWidget != newWidget) {
                    changeViewFor(newWidget)
                    changes = true
                }
            } else {

                addViewFor(newWidget, map)
                changes = true
            }
        }

        // Delete unused widgets
        for (id: Long in widgetCheckMap.keys) {
            if (!widgetCheckMap[id]!!) {
                removeViewFor(widgetEntryMap[id])
                changes = true
            }
        }
        widgetList.clear()
        widgetList.addAll((newWidgets))
        if (changes) {
            requestLayout()
        }
    }

    private fun addViewFor(entity: BaseEntity, map: HashMap<Topic?, AbstractNode>) {
        Log.i(TAG, "Add view for " + entity.name)
        val baseView: IBaseView = createViewFrom(entity) ?: return
        baseView.widgetEntity = (entity)

        // Check if view is a group view and register the sub layers
        if (baseView is WidgetGroupView) {
            for (subEntity: BaseEntity in entity.childEntities) {
                val subView: IBaseView? = createViewFrom(subEntity)
                subView?.let{
                    subView.widgetEntity = (subEntity)
                    if (subView is ISubscriberView) {
                        if (map[subEntity.topic] != null) {

                            val lastData: RosData? = map[subEntity.topic]?.lastRosData
                            if (lastData != null) {
                                subView.onNewMessage(lastData.message)
                            }
                        }
                    }
                    if (subView !is LayerView) return
                    baseView.addLayer(subView)
                }
            }
        }

        // Set data listener if view is a publisher
        if (baseView is IPublisherView) {
            baseView.dataListener = DataListener { data: BaseData? ->
                data?.let { informDataChange(it) }
            }
        }

        // Add as subview if the view is a widget view
        if (baseView is WidgetView) {
            this.addView(baseView as WidgetView?)
        }
    }

    private fun createViewFrom(entity: BaseEntity): IBaseView? {
        // Create actual widget view object
        val classPath: String = (BuildConfig.APPLICATION_ID
                + String.format(
            Constants.VIEW_FORMAT,
            entity.type!!.lowercase(Locale.getDefault()),
            entity.type
        ))
        val `object`: Any
        try {
            val clazz: Class<*> = Class.forName(classPath)
            val constructor: Constructor<*> = clazz.getConstructor(Context::class.java)
            `object` = constructor.newInstance(context)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        Log.i(TAG, "object is a : " + `object`.javaClass.canonicalName)
        if (`object` !is IBaseView) {
            Log.i(TAG, "View can not be created from: $classPath")
            return null
        }
        return `object`
    }

    private fun changeViewFor(entity: BaseEntity?) {
        Log.i(TAG, "Change view for " + entity!!.name)
        for (i in 0 until childCount) {
            val view: IBaseView = getChildAt(i) as IBaseView
            if (view.sameWidgetEntity(entity)) {
                view.widgetEntity = (entity)
                return
            }
        }
    }

    private fun removeViewFor(entity: BaseEntity?) {
        Log.i(TAG, "Remove view for " + entity!!.name)
        for (i in 0 until childCount) {
            val view: IBaseView = getChildAt(i) as IBaseView
            if (view.sameWidgetEntity(entity)) {
                removeView(view as WidgetView?)
                return
            }
        }
    }

    val widgets: List<BaseEntity?>
        get() {
            return widgetList
        }

    fun setDataListener(listener: DataListener?) {
        dataListener = listener
    }

    fun setOnWidgetDetailsChanged(listener: WidgetChangeListener?) {
        widgetDetailsChangedListener = listener
    }

    fun setVizEditMode(enabled: Boolean) {
        vizEditMode = enabled
        for (i in 0 until childCount) {
            val widgetView: WidgetView = getChildAt(i) as WidgetView
            widgetView.setOnScaleListener(tileWidth) {
                    baseEntity: BaseEntity, updateConfig: Boolean ->
                if (vizEditMode) {
                    widgetScaleShadowPosition = (baseEntity as IPositionEntity).getPosition()
                    widgetScaleShadowPosition.height = max(
                        0.0,
                        min(widgetScaleShadowPosition.height.toDouble(), tilesY.toDouble())
                    )
                        .toInt()
                    widgetScaleShadowPosition.width = max(
                        0.0,
                        min(widgetScaleShadowPosition.width.toDouble(), tilesX.toDouble())
                    )
                        .toInt()
                    (baseEntity as IPositionEntity).setPosition(widgetScaleShadowPosition)
                    drawWidgetScaleShadow = !updateConfig
                    invalidate()
                    if (updateConfig) {
                        widgetDetailsChangedListener!!.onWidgetDetailsChanged(baseEntity)
                    }
                }
            }
            widgetView.editMode = (enabled)
        }
    }

    companion object {
        val TAG: String = WidgetViewGroup::class.java.simpleName
        val TILES_X: Int = 8
    }
}