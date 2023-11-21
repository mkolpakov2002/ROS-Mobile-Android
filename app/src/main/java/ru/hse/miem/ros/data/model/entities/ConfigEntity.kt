package ru.hse.miem.ros.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.2
 * @created on 30.01.20
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 * @updated on 01.10.20
 * @modified by Maxim Kolpakov
 */
@Entity(tableName = "config_table")
class ConfigEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var creationTime: Long = 0
    var lastUsed: Long = 0
    var name : String? = "DefaultName"
    var isFavourite = false
}