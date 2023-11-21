package ru.hse.miem.ros.widgets.pose

import android.view.View
import geometry_msgs.PoseWithCovarianceStamped
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberLayerViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 21.03.21
 */
class PoseDetailVH() : SubscriberLayerViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {}
    override fun updateEntity(entity: BaseEntity) {}
    public override fun getTopicTypes(): List<String> {
        return listOf(PoseWithCovarianceStamped._TYPE)
    }
}
