package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM measurements ORDER BY timestamp DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM measurements WHERE siteCode = :siteCode ORDER BY timestamp DESC")
    fun getMeasurementsBySite(siteCode: String): Flow<List<MeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity): Long

    @Delete
    suspend fun deleteMeasurement(measurement: MeasurementEntity)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM measurements")
    suspend fun clearAll()
}
