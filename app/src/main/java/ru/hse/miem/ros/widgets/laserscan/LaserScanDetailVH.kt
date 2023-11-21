package ru.hse.miem.ros.widgets.laserscan

import android.content.DialogInterface
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import com.skydoves.colorpickerview.AlphaTileView
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberLayerViewHolder
import ru.hse.miem.ros.utility.Utils
import sensor_msgs.LaserScan
import kotlin.math.max
import kotlin.math.min

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 14.05.21
 */
class LaserScanDetailVH() : SubscriberLayerViewHolder(), OnEditorActionListener {
    private lateinit var areaTileView: AlphaTileView
    private lateinit var pointsTileView: AlphaTileView
    private lateinit var pointSizeEditText: EditText
    private var pointsColor: Int = 0
    private var areaColor: Int = 0
    override fun initView(itemView: View) {
        pointsTileView = itemView.findViewById(R.id.pointsTileView)
        areaTileView = itemView.findViewById(R.id.areaTileView)
        pointSizeEditText = itemView.findViewById(R.id.pointSizeEditText)
        pointsTileView.setOnClickListener { v: View? ->
            openColorChooser(
                pointsTileView
            )
        }
        areaTileView.setOnClickListener { v: View? ->
            openColorChooser(
                areaTileView
            )
        }
        pointSizeEditText.setOnEditorActionListener(this)
    }

    override fun bindEntity(entity: BaseEntity) {
        val scanEntity: LaserScanEntity = entity as LaserScanEntity
        pointSizeEditText.setText(scanEntity.pointSize.toString())
        chooseColor(pointsTileView, scanEntity.pointsColor)
        chooseColor(areaTileView, scanEntity.areaColor)
    }

    private fun openColorChooser(tileView: AlphaTileView?) {
        Log.i(TAG, "OPen stuff")
        itemView?.let{
            val builder: ColorPickerDialog.Builder = ColorPickerDialog.Builder(it.context)
                .setTitle("Choose a color")
                .setPositiveButton(
                    R.string.ok,
                    ColorEnvelopeListener { envelope: ColorEnvelope, _: Boolean ->
                        chooseColor(tileView, envelope.color)
                        forceWidgetUpdate()
                    } as ColorEnvelopeListener?
                )
                .setNegativeButton(R.string.cancel
                ) { dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss() }
            var initialColor: Int = areaColor
            if (tileView === pointsTileView) {
                initialColor = pointsColor
            }
            builder.colorPickerView.setInitialColor(initialColor)
            builder.show()
        }
    }

    private fun chooseColor(tileView: AlphaTileView?, color: Int) {
        if (tileView === pointsTileView) {
            pointsColor = color
            pointsTileView.setBackgroundColor(color)
        } else if (tileView === areaTileView) {
            areaColor = color
            areaTileView.setBackgroundColor(color)
        }
    }

    override fun updateEntity(entity: BaseEntity) {
        val scanEntity: LaserScanEntity = entity as LaserScanEntity
        scanEntity.pointsColor = pointsColor
        scanEntity.areaColor = areaColor
        var size: Int = pointSizeEditText.text.toString().toInt()
        size = max(
            MIN_POINT_SIZE.toDouble(),
            min(MAX_POINT_SIZE.toDouble(), size.toDouble())
        )
            .toInt()
        scanEntity.pointSize = size
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(LaserScan._TYPE)
    }

    public override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
        when (actionId) {
            EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT, EditorInfo.IME_ACTION_PREVIOUS -> {
                Utils.hideSoftKeyboard(v)
                v.clearFocus()
                forceWidgetUpdate()
                return true
            }
        }
        return false
    }

    companion object {
        private val TAG: String = LaserScanDetailVH::class.java.simpleName
        private val MIN_POINT_SIZE: Int = 1
        private val MAX_POINT_SIZE: Int = 50
    }
}
