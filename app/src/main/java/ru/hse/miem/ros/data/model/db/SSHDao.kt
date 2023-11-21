package ru.hse.miem.ros.data.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.hse.miem.ros.data.model.entities.SSHEntity

/**
 * TODO: Description
 *
 * @author Tanya Rykova
 * @version 1.0.1
 * @created on 04.06.20
 * @updated on 01.10.20
 * @modified by Maxim Kolpakov
 */
@Dao
abstract class SSHDao : BaseDao<SSHEntity> {
    @Query("SELECT * FROM ssh_table WHERE configId = :configId LIMIT 1")
    abstract fun getSSH(configId: Long): LiveData<SSHEntity>
    @Query("DELETE FROM ssh_table WHERE configId = :configId")
    abstract suspend fun delete(configId: Long)
    @Query("DELETE FROM ssh_table")
    abstract suspend fun deleteAll()
}
