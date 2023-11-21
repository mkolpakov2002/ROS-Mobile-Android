package ru.hse.miem.ros.widgets.touchgoal

import android.view.View
import geometry_msgs.PoseStamped
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.PublisherLayerViewHolder

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.05.2021
 */
class TouchGoalDetailVH() : PublisherLayerViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {
        val scanEntity: TouchGoalEntity? = entity as TouchGoalEntity?
    }

    override fun updateEntity(entity: BaseEntity) {
        val scanEntity: TouchGoalEntity = entity as TouchGoalEntity
    }

    public override fun getTopicTypes(): List<String> {
        return listOf(PoseStamped._TYPE)
    }

    companion object {
        private val TAG: String = TouchGoalDetailVH::class.java.simpleName
    }
}
