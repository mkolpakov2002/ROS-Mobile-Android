package ru.hse.miem.ros.data.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO: Description
 *
 *
 * Replaced version of Base Entity.
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 23.09.20
 * @updated on
 * @modified by
 */
@Entity(tableName = "widget_table")
class WidgetStorageData {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "type_name")
    var typeName: String? = null

    @ColumnInfo(name = "widget_config_id")
    var configId: Long = 0

    @ColumnInfo(name = "data")
    var data: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null
}
