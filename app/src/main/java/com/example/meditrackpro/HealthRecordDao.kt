package com.example.meditrackpro

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HealthRecordDao {
    @Query("SELECT * FROM health_records ORDER BY id DESC")
    fun getAllRecords(): LiveData<List<HealthRecord>>

    @Query("SELECT COUNT(*) FROM health_records WHERE type = :type")
    suspend fun countByType(type: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecord)

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("DELETE FROM health_records")
    suspend fun clearAll()
}