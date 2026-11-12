package com.example.data

import kotlinx.coroutines.flow.Flow

class MeasurementRepository(private val dao: MeasurementDao) {

    fun getAllMeasurements(): Flow<List<MeasurementEntity>> = dao.getAllMeasurements()

    fun getMeasurementsBySite(siteCode: String): Flow<List<MeasurementEntity>> =
        dao.getMeasurementsBySite(siteCode)

    suspend fun saveMeasurement(measurement: MeasurementEntity): Long =
        dao.insertMeasurement(measurement)

    suspend fun deleteMeasurement(measurement: MeasurementEntity) =
        dao.deleteMeasurement(measurement)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()
}
