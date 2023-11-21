package ru.hse.miem.ros.widgets.camera

import android.view.View
import ru.hse.miem.ros.data.model.entities.widgets.BaseEntity
import ru.hse.miem.ros.ui.views.details.SubscriberWidgetViewHolder
import sensor_msgs.CompressedImage
import sensor_msgs.Image

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 13.05.20
 * @updated on 07.09.20
 * @modified by Maxim Kolpakov
 * @updated on 17.09.20
 * @modified by Tanya Rykova
 * @updated on 20.03.21
 * @modified by Maxim Kolpakov
 */
class CameraDetailVH() : SubscriberWidgetViewHolder() {
    override fun initView(itemView: View) {}
    override fun bindEntity(entity: BaseEntity) {}
    override fun updateEntity(entity: BaseEntity) {}
    public override fun getTopicTypes(): List<String> {
        return listOf(Image._TYPE, CompressedImage._TYPE)
    }

    companion object {
        val TAG: String = CameraDetailVH::class.java.simpleName
    }
}
