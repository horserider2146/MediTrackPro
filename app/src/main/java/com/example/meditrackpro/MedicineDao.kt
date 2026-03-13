package com.example.meditrackpro

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MedicineDao {

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): LiveData<List<Medicine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: Medicine)

    @Delete
    suspend fun deleteMedicine(medicine: Medicine)

    @Query("DELETE FROM medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: Int)
    @Query("DELETE FROM medicines")
    suspend fun clearAllMedicines()
}