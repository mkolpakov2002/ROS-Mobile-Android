package ru.hse.miem.ros.widgets.gridmap

import android.view.View
import nav_msgs.OccupancyGrid
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberLayerViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class GridMapDetailVH() : SubscriberLayerViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {}
    override fun updateEntity(entity: BaseEntity) {}
    public override fun getTopicTypes(): List<String> {
        return listOf(OccupancyGrid._TYPE)
    }
}
