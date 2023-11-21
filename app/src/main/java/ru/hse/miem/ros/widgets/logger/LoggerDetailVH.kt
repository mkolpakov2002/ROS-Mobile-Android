package ru.hse.miem.ros.widgets.logger

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import ru.hse.miem.ros.utility.Utils

/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Tanya Rykova
 * @updated on 20.03.2021
 * @modified by Maxim Kolpakov
 */
class LoggerDetailVH() : SubscriberWidgetViewHolder() {
    private lateinit var labelTextRotationSpinner: Spinner
    private lateinit var rotationAdapter: ArrayAdapter<CharSequence>
    public override fun initView(itemView: View) {
        labelTextRotationSpinner = itemView.findViewById(R.id.loggerTextRotation)
        rotationAdapter = ArrayAdapter.createFromResource(
            itemView.context,
            R.array.button_rotation, android.R.layout.simple_spinner_dropdown_item
        )
        labelTextRotationSpinner.adapter = rotationAdapter
    }

    override fun bindEntity(entity: BaseEntity) {
        val loggerEntity: LoggerEntity = entity as LoggerEntity
        val degrees: String = Utils.numberToDegrees(
            loggerEntity.rotation
        )
        labelTextRotationSpinner.setSelection(rotationAdapter.getPosition(degrees))
    }

    override fun updateEntity(entity: BaseEntity) {
        val loggerEntity: LoggerEntity = entity as LoggerEntity
        val degrees: String = labelTextRotationSpinner.selectedItem.toString()
        loggerEntity.rotation = Utils.degreesToNumber(degrees)
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(std_msgs.String._TYPE)
    }
}
