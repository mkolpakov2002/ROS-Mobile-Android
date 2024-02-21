package ru.hse.miem.ros.data.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import ru.hse.miem.ros.data.model.entities.MasterEntity

@Dao
abstract class MasterDao : BaseDao<MasterEntity> {
    @Query("SELECT * FROM master_table WHERE configId = :configId LIMIT 1")
    abstract fun getMaster(configId: Long): LiveData<MasterEntity?>
    @Query("DELETE FROM master_table WHERE configId = :configId")
    abstract suspend fun delete(configId: Long)
    @Query("DELETE FROM master_table")
    abstract suspend fun deleteAll()
}
