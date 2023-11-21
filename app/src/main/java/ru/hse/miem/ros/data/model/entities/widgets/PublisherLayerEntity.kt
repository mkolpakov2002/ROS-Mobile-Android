package ru.hse.miem.ros.data.model.entities.widgets

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 24.09.20
 */
abstract class PublisherLayerEntity : BaseEntity(), I2DLayerEntity, IPublisherEntity {
    var publishRate = 1f
    var immediatePublish = false

    override fun equalRosState(other: BaseEntity): Boolean {
        if (other !is PublisherLayerEntity) return false
        return super.equalRosState(other)
                && publishRate == other.publishRate
                && immediatePublish == other.immediatePublish
    }
}
