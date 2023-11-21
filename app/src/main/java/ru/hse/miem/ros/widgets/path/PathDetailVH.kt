package ru.hse.miem.ros.widgets.path

import android.view.View
import nav_msgs.Path
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberLayerViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 08.03.21
 */
class PathDetailVH() : SubscriberLayerViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {}
    override fun updateEntity(entity: BaseEntity) {}
    public override fun getTopicTypes(): List<String> {
        return listOf(Path._TYPE)
    }
}
