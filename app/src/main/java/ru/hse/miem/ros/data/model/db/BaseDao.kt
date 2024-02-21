package ru.hse.miem.ros.data.model.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(obj: T)

    @Delete
    suspend fun delete(obj: T): Int
}
