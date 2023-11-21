package ru.hse.miem.ros.data.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 26.07.20
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 */
@Entity(tableName = "widget_count_table")
class WidgetCountEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "widget_config_id")
    var configId: Long = 0

    @ColumnInfo(name = "widget_type")
    var type: String? = null

    @ColumnInfo(name = "widget_count")
    var count: Long = 0
}
