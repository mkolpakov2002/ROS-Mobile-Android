package ru.hse.miem.ros.widgets.battery

import android.view.View
import android.widget.CompoundButton
import com.google.android.material.switchmaterial.SwitchMaterial
import ru.hse.miem.ros.R
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import sensor_msgs.BatteryState

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 13.05.2021
 */
class BatteryDetailVH() : SubscriberWidgetViewHolder() {
    lateinit var voltageSwitch: SwitchMaterial
    var forceSetChecked: Boolean = false
    public override fun initView(itemView: View) {
        voltageSwitch = itemView.findViewById(R.id.voltageSwitch)
        voltageSwitch.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
            if (!forceSetChecked) forceWidgetUpdate()
        }
    }

    override fun bindEntity(entity: BaseEntity) {
        val batteryEntity: BatteryEntity = entity as BatteryEntity
        forceSetChecked = true
        voltageSwitch.setChecked(batteryEntity.displayVoltage)
        forceSetChecked = false
    }

    override fun updateEntity(entity: BaseEntity) {
        val batteryEntity: BatteryEntity? = entity as BatteryEntity?
        batteryEntity!!.displayVoltage = voltageSwitch.isChecked
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(BatteryState._TYPE)
    }
}
