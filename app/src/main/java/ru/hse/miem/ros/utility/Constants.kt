package ru.hse.miem.ros.utility

/**
 * Constants of the project that are globally reachable
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 31.01.20
 * @updated on 31.01.20
 * @modified by
 */
object Constants {
    /**
     * Name of the room database on disk
     */
    val DB_NAME: String = "config_database"
    val VIEW_FORMAT: String = ".widgets.%s.%sView"
    val VIEWHOLDER_FORMAT: String = ".widgets.%s.%sDetailVH"
    val ENTITY_FORMAT: String = ".widgets.%s.%sEntity"
    val DETAIL_LAYOUT_FORMAT: String = "widget_detail_%s"
    val WIDGET_NAMING: String = "%s #%d"
}
