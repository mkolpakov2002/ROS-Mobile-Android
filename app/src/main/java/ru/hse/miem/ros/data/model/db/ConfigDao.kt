package ru.hse.miem.ros.data.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.hse.miem.ros.data.model.entities.ConfigEntity

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.1
 * @created on 31.01.20
 * @updated on 04.06.20
 * @modified by Tanya Rykova
 * @updated on 27.07.20
 * @modified by Tanya Rykova
 * @updated on 23.09.20
 * @modified by Maxim Kolpakov
 */
@Dao
abstract class ConfigDao : BaseDao<ConfigEntity> {
    @get:Query("SELECT * FROM config_table")
    abstract val allConfigs: LiveData<List<ConfigEntity>>
    @Query("SELECT * FROM config_table where id = :id")
    abstract fun getConfig(id: Long): LiveData<ConfigEntity>

    @get:Query("SELECT * FROM config_table ORDER BY creationTime DESC LIMIT 1")
    abstract val latestConfig: LiveData<ConfigEntity>

    @get:Query("SELECT * FROM config_table ORDER BY creationTime DESC LIMIT 1")
    abstract val latestConfigDirect: ConfigEntity
    @Query("DELETE FROM config_table where id = :id")
    abstract suspend fun removeConfig(id: Long)
    @Query("DELETE FROM config_table")
    abstract suspend fun deleteAll()

    companion object {
        val TAG = ConfigDao::class.java.canonicalName
    }
}