package ru.hse.miem.ros.data.model.entities.widgets

import ru.hse.miem.ros.ui.general.Position

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
    private var posX = 0
    private var posY = 0
    var width = 0
    var height = 0
}
