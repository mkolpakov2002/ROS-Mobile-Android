package ru.hse.miem.ros.widgets.joystick

import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import geometry_msgs.Twist
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.PublisherWidgetViewHolder
import java.util.Locale

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 13.02.20
 * @updated on 20.05.20
 * @modified by Maxim Kolpakov
 * @updated on 05.11.2020
 * @modified by Maxim Kolpakov
 */
class JoystickDetailVH() : PublisherWidgetViewHolder() {
    private var forceSetChecked: Boolean = false
    private lateinit var xDirSpinner: Spinner
    private lateinit var xAxisSpinner: Spinner
    private lateinit var xScaleLeft: EditText
    private lateinit var xScaleRight: EditText
    private lateinit var xScaleMiddle: TextView
    private lateinit var yDirSpinner: Spinner
    private lateinit var yAxisSpinner: Spinner
    private lateinit var yScaleLeft: EditText
    private lateinit var yScaleRight: EditText
    private lateinit var yScaleMiddle: TextView
    private lateinit var stickLimitBox: CheckBox
    private lateinit var xDirAdapter: ArrayAdapter<CharSequence>
    private lateinit var xAxisAdapter: ArrayAdapter<CharSequence>
    private lateinit var yDirAdapter: ArrayAdapter<CharSequence>
    private lateinit var yAxisAdapter: ArrayAdapter<CharSequence>
    public override fun initView(itemView: View) {
        xDirSpinner = itemView.findViewById(R.id.xDirSpinner)
        xAxisSpinner = itemView.findViewById(R.id.xAxisSpinner)
        xScaleLeft = itemView.findViewById(R.id.xScaleLeft)
        xScaleRight = itemView.findViewById(R.id.xScaleRight)
        xScaleMiddle = itemView.findViewById(R.id.xScaleMiddle)
        yDirSpinner = itemView.findViewById(R.id.yDirSpinner)
        yAxisSpinner = itemView.findViewById(R.id.yAxisSpinner)
        yScaleLeft = itemView.findViewById(R.id.yScaleLeft)
        yScaleRight = itemView.findViewById(R.id.yScaleRight)
        yScaleMiddle = itemView.findViewById(R.id.yScaleMiddle)
        stickLimitBox = itemView.findViewById(R.id.stickLimitBox)
        stickLimitBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener {
            buttonView: CompoundButton?, isChecked: Boolean ->
            if (!forceSetChecked) forceWidgetUpdate()
        }))

        // Init spinner
        xDirAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.joystick_twist_dir, android.R.layout.simple_spinner_dropdown_item
        )
        xAxisAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.joystick_twist_axis, android.R.layout.simple_spinner_dropdown_item
        )
        yDirAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.joystick_twist_dir, android.R.layout.simple_spinner_dropdown_item
        )
        yAxisAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.joystick_twist_axis, android.R.layout.simple_spinner_dropdown_item
        )
        xDirSpinner.adapter = xDirAdapter
        xAxisSpinner.adapter = xAxisAdapter
        yDirSpinner.adapter = yDirAdapter
        yAxisSpinner.adapter = yAxisAdapter
    }

    public override fun bindEntity(entity: BaseEntity) {
        val widget: JoystickEntity = entity as JoystickEntity
        val xAxisMapping: Array<String> =
            widget.xAxisMapping.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        xDirSpinner.setSelection(xDirAdapter.getPosition(xAxisMapping[0]))
        xAxisSpinner.setSelection(xAxisAdapter.getPosition(xAxisMapping[1]))
        val yAxisMapping: Array<String> =
            widget.yAxisMapping.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        yDirSpinner.setSelection(yDirAdapter.getPosition(yAxisMapping[0]))
        yAxisSpinner.setSelection(yAxisAdapter.getPosition(yAxisMapping[1]))
        xScaleLeft.setText(String.format(Locale.US, "%.2f", widget.xScaleLeft))
        xScaleRight.setText(String.format(Locale.US, "%.2f", widget.xScaleRight))
        xScaleMiddle.text = String.format(
            Locale.US,
            "%.2f",
            (widget.xScaleRight + widget.xScaleLeft) / 2
        )
        yScaleLeft.setText(String.format(Locale.US, "%.2f", widget.yScaleLeft))
        yScaleRight.setText(String.format(Locale.US, "%.2f", widget.yScaleRight))
        yScaleMiddle.text = String.format(
            Locale.US,
            "%.2f",
            (widget.yScaleRight + widget.yScaleLeft) / 2
        )
        forceSetChecked = true
        stickLimitBox.isChecked = widget.rectangularLimits
        forceSetChecked = false
    }

    public override fun updateEntity(entity: BaseEntity) {
        val widget: JoystickEntity = entity as JoystickEntity

        // Update joystick parameters
        widget.xAxisMapping =
            xDirSpinner.selectedItem.toString() + "/" + xAxisSpinner.selectedItem
        widget.yAxisMapping =
            yDirSpinner.selectedItem.toString() + "/" + yAxisSpinner.selectedItem
        for (str: String in arrayOf(
            "xScaleLeft",
            "xScaleRight",
            "yScaleLeft",
            "yScaleRight"
        )) {
            try {
                val editText: EditText =
                    this.javaClass.getDeclaredField(str).get(this) as EditText
                val value: Float = editText.text.toString().toFloat()
                widget.javaClass.getField(str).set(entity, value)
            } catch (ignored: Exception) {
            }
        }
        widget.rectangularLimits = stickLimitBox.isChecked
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(Twist._TYPE)
    }
}
