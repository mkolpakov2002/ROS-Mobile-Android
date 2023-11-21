package ru.hse.miem.ros.widgets.gps

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import sensor_msgs.NavSatFix

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 05.05.20
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 * @updated on 20.03.21
 * @modified by Maxim Kolpakov
 */
class GpsDetailVH() : SubscriberWidgetViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {}
    override fun updateEntity(entity: BaseEntity) {}
    public override fun getTopicTypes(): List<String> {
        return listOf(NavSatFix._TYPE)
    }
}
