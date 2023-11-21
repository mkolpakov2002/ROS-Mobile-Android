package ru.hse.miem.ros.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.0
 * @created on 04.06.20
 */
@Entity(tableName = "ssh_table")
class SSHEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var configId: Long = 0
    var ip = "192.168.1.1"
    var port = 22
    var username = "pi"
    var password = "raspberry"
}