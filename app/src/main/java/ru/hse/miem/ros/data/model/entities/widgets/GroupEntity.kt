package ru.hse.miem.ros.data.model.entities.widgets

import ru.hse.miem.ros.ui.general.Position

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 * @updated on
 * @modified by
 */
open class GroupEntity(): BaseEntity(), IPositionEntity, ISubscriberEntity, IPublisherEntity {
    override fun getPosition(): Position {
        return Position(posX, posY, width, height)
    }
    override fun setPosition(position: Position) {
        posX = position.x
        posY = position.y
        width = position.width
        height = position.height
    }
    var posX = 0
    var posY = 0
    var width = 0
    var height = 0
}
