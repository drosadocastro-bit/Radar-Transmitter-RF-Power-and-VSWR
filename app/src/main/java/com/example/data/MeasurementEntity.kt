package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val siteCode: String, // "SJU" or "STT"
    val channelCode: String, // "CH A" or "CH B"
    val timestamp: Long = System.currentTimeMillis(),
    val incidentCouplerDb: Double,
    val revCouplerDb: Double,
    val fwdReadingMw: Double,
    val fwdReadingDb: Double,
    val revReadingDbAbs: Double,
    val peakPowerMw: Double,
    val peakPowerKw: Double,
    val vswr: Double,
    val returnLossDb: Double,
    val statusLabel: String,
    val technicianName: String = "",
    val notes: String = ""
)
