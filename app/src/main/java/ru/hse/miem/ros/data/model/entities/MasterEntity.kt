package ru.hse.miem.ros.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.1
 * @created on 30.01.20
 * @updated on 31.01.20
 * @modified by
 */
@Entity(tableName = "master_table")
class MasterEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var configId: Long = 0
    var ip = "192.168.0.0"
    var port = 11311
}
