package ru.hse.miem.ros.data.model.entities.widgets

import ru.hse.miem.ros.ui.general.Position
/**
 * Entity with positional information to be able to display it
 * in the visualisation view as a stand-alone widget.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 10.03.21
 */
interface IPositionEntity {
    fun getPosition(): Position

    fun setPosition(position: Position)
}
