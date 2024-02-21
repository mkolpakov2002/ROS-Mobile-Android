package ru.hse.miem.ros.data.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.hse.miem.ros.data.model.entities.ConfigEntity

@Dao
abstract class ConfigDao : BaseDao<ConfigEntity> {
    @Query("SELECT * FROM config_table")
    abstract fun getAllConfigs(): LiveData<List<ConfigEntity>>
    @Query("SELECT * FROM config_table where id = :id")
    abstract fun getConfig(id: Long): LiveData<ConfigEntity>

    @Query("SELECT * FROM config_table ORDER BY creationTime DESC LIMIT 1")
    abstract fun getLatestConfig(): LiveData<ConfigEntity>

    @Query("SELECT * FROM config_table ORDER BY creationTime DESC LIMIT 1")
    abstract suspend fun getLatestConfigDirect(): ConfigEntity
    @Query("DELETE FROM config_table where id = :id")
    abstract suspend fun removeConfig(id: Long)
    @Query("DELETE FROM config_table")
    abstract suspend fun deleteAll()

    companion object {
        val TAG = ConfigDao::class.java.canonicalName
    }
}