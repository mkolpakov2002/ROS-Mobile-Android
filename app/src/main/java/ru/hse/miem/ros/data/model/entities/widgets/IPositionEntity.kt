package ru.hse.miem.ros.data.model.entities.widgets

import ru.hse.miem.ros.ui.general.Position
/**
 * Entity with positional information to be able to display it
 * in the visualisation view as a stand-alone widget.
 *
 * @author Maxim Kolpakov
 */
interface IPositionEntity {
    fun getPosition(): Position

    fun setPosition(position: Position)
}
