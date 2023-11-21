package ru.hse.miem.ros.widgets.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import androidx.core.content.ContextCompat
import ru.hse.miem.ros.R
import ru.hse.miem.ros.ui.views.widgets.SubscriberWidgetView
import ru.hse.miem.ros.utility.Utils
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.ros.internal.message.Message
import sensor_msgs.NavSatFix
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 05.05.20
 * @updated on 05.05.20
 * @modified by
 */
// TODO: Add maybe a button for getting back to gps position
class GpsView : SubscriberWidgetView {
    // Open Street Map (OSM)
    private val REQUEST_PERMISSIONS_REQUEST_CODE: Int = 1
    private val locationGeoPoint: GeoPoint = GeoPoint(55.803390, 37.410207)
    private val centerGeoPoint: GeoPoint = GeoPoint(55.803390, 37.410207)
    private val dragSensitivity: Double = 0.05
    private val hadLongPressed: Boolean = false
    private lateinit var mapController: IMapController

    // Rectangle Surrounding
    lateinit var paint: Paint
    private var cornerWidth: Float = 0f

    // Grid Map Information
    lateinit var data: GpsData
    private lateinit var map: MapView

    // Zoom Parameters, TODO: Add this into details
    private var minZoom: Double = 1.0 // min. and max. zoom
    private var maxZoom: Double = 18.0
    private var zoomScale: Float = 1f
    private var scaleFactor: Float = 18f
    private lateinit var detector: ScaleGestureDetector
    private var mode: Int = 0
    private var startX: Float = 0f // finger position tracker
    private var startY: Float = 0f
    private var translateX: Float = 0f // Amount of translation
    private var translateY: Float = 0f
    private var moveLat: Double = 0.0
    private var moveLon: Double = 0.0
    private val gestureDetector: GestureDetector = GestureDetector(object : SimpleOnGestureListener() {
        public override fun onLongPress(e: MotionEvent) {
            moveLat = 0.0
            moveLon = 0.0
        }
    })
    private var accLat: Double = 0.0
    private var accLon: Double = 0.0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        cornerWidth = Utils.dpToPx(context, 8f)
        paint = Paint()
        paint.color = resources.getColor(R.color.whiteHigh)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        // OSM (initialize the map)
        val ctx: Context = context
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map = MapView(context, null, null)
        map.setTileSource(TileSourceFactory.MAPNIK)
        requestPermissionsIfNecessary(
            arrayOf( // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)
        minZoom = map.minZoomLevel
        maxZoom = map.maxZoomLevel

        // Map controller
        mapController = map.controller

        // Touch
        detector = ScaleGestureDetector(context, ScaleListener())
    }

    public override fun onTouchEvent(event: MotionEvent): Boolean {
        if (editMode) {
            return super.onTouchEvent(event)
        }
        var dragged = false
        gestureDetector.onTouchEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mode = DRAG
                startX = event.x
                startY = event.y
            }

            MotionEvent.ACTION_MOVE -> {
                translateX = event.x - startX
                translateY = event.y - startY
                val distance: Double = sqrt(
                    (event.x - startX).toDouble().pow(2.0) + (event.y - startY).toDouble().pow(2.0)
                )
                if (distance > 0) {
                    dragged = true
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> mode = ZOOM
            MotionEvent.ACTION_UP -> {
                mode = NONE
                dragged = false
            }

            MotionEvent.ACTION_POINTER_UP -> mode = DRAG
        }
        // Activate Zoom
        detector.onTouchEvent(event)

        // Redraw the canvas
        if (((mode == DRAG) && (scaleFactor != 1f) && dragged) || mode == ZOOM) {
            this.invalidate()
        }
        return true
    }

    public override fun onDraw(canvas: Canvas) {
        Log.i(TAG, "On Draw")
        super.onDraw(canvas)
        canvas.save()

        // Get vizualization size
        val left = 0f
        val right = 0f
        val width: Float = width.toFloat()
        val height: Float = height.toFloat()

        // Set overlay item
        val overlayItem = OverlayItem("Position", "Robot", locationGeoPoint)
        val overlayItemArrayList: ArrayList<OverlayItem> = ArrayList()
        overlayItemArrayList.add(overlayItem)
        val locationOverlay: ItemizedOverlay<OverlayItem> =
            ItemizedIconOverlay(getContext(), overlayItemArrayList, null)

        // Move the map to specific location
        zoomScale = 2.0.pow(scaleFactor.toDouble()).toFloat()

        // Just separating acceleration component
        accLat = (translateY / zoomScale) * dragSensitivity
        accLon = (translateX / zoomScale) * dragSensitivity
        moveLat += accLat
        moveLon -= accLon

        // Resets dynamics, otherwise every time GPS publishes it keeps scrolling the map
        translateY = 0f
        translateX = 0f
        centerGeoPoint.latitude = locationGeoPoint.latitude + moveLat
        centerGeoPoint.longitude = locationGeoPoint.longitude + moveLon
        mapController.setCenter(centerGeoPoint)
        mapController.setZoom(scaleFactor.toDouble())
        map.requestLayout()

        // Draw the OMS
        map.layout(left.toInt(), right.toInt(), width.toInt(), height.toInt())
        map.overlays.add(locationOverlay)
        map.draw(canvas)

        // Apply the changes
        canvas.restore()
        // Put a rectangle around
        canvas.drawRoundRect(left, right, width, height, cornerWidth, cornerWidth, (paint))
    }

    public override fun onNewMessage(message: Message) {
        data = GpsData(message as NavSatFix)
        locationGeoPoint.latitude = data.lat
        locationGeoPoint.longitude = data.lon
        this.invalidate()
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission: String in permissions) {
            if ((ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
    }

    private inner class ScaleListener() : SimpleOnScaleGestureListener() {
        public override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(
                (minZoom.toFloat()).toDouble(),
                min(scaleFactor.toDouble(), (maxZoom.toFloat()).toDouble())
            ).toFloat()
            return true
        }
    }

    companion object {
        val TAG: String = GpsView::class.java.simpleName
        private val NONE: Int = 0 // mode
        private val DRAG: Int = 1
        private val ZOOM: Int = 2
    }
}
