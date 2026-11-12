package com.example.data

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

data class CalculationResult(
    val incidentCouplerDb: Double,
    val revCouplerDb: Double,
    val fwdReadingMw: Double,
    val fwdReadingDb: Double,
    val revReadingDbAbs: Double,
    val fwdTotalDbm: Double,
    val revTotalDbm: Double,
    val returnLossDb: Double,
    val reflectionCoefficient: Double,
    val vswr: Double,
    val peakPowerMw: Double,
    val peakPowerKw: Double,
    val status: VswrStatus
)

enum class VswrStatus(val label: String, val colorHex: Long) {
    EXCELLENT("Excellent (< 1.20)", 0xFF2E7D32),
    NORMAL("Normal (1.20 - 1.30)", 0xFF0077B6),
    ACCEPTABLE("Acceptable (1.30 - 1.40)", 0xFFF57C00),
    CRITICAL("High SWR (> 1.40)", 0xFFD32F2F)
}

object RfCalculator {

    fun mwToDb(mw: Double): Double {
        if (mw <= 0.0) return 0.0
        val db = 10.0 * log10(mw)
        return (db * 100.0).roundToInt() / 100.0
    }

    fun dbToMw(db: Double): Double {
        val mw = 10.0.pow(db / 10.0)
        return (mw * 100.0).roundToInt() / 100.0
    }

    /**
     * Calculates RF Peak Power and VSWR according to transmitter directional coupler parameters.
     *
     * @param incidentCouplerDb Attenuation of the forward coupler in dB (e.g. 54.18)
     * @param revCouplerDb Attenuation of the reverse coupler in dB (e.g. 44.87)
     * @param fwdReadingDb Forward power meter reading in dB (e.g. 2.55)
     * @param fwdReadingMw Forward power meter reading in mW (e.g. 1.81)
     * @param revReadingDbAbs Reflected power meter reading in dB absolute value (e.g. 9.55)
     * @param dutyFactor Calibration factor for duty cycle / PRF to peak power (e.g. 619.71 for SJU, 625.8 for STT)
     * @param padOffset Calibration pad/insertion loss offset on reflected port in dB
     */
    fun calculate(
        incidentCouplerDb: Double,
        revCouplerDb: Double,
        fwdReadingDb: Double,
        fwdReadingMw: Double = dbToMw(fwdReadingDb),
        revReadingDbAbs: Double,
        dutyFactor: Double = 620.0,
        padOffset: Double = 0.0
    ): CalculationResult {
        // Forward power in dBm: Coupler attenuation + Meter reading
        val fwdTotalDbm = incidentCouplerDb + fwdReadingDb

        // Average forward power in Watts
        val avgPowerWatts = 10.0.pow((fwdTotalDbm - 30.0) / 10.0)

        // Peak power in Megawatts (MW) and Kilowatts (kW)
        val peakPowerMwRaw = if (dutyFactor > 0.0) avgPowerWatts / dutyFactor else 0.0
        val peakPowerMw = (peakPowerMwRaw * 100.0).roundToInt() / 100.0
        val peakPowerKw = (peakPowerMwRaw * 1000.0 * 10.0).roundToInt() / 10.0

        // Reflected power in dBm: Coupler attenuation - Absolute reading + Pad offset
        val revTotalDbm = revCouplerDb - revReadingDbAbs + padOffset

        // Return Loss (RL) in dB: Forward power - Reflected power
        val returnLossDb = fwdTotalDbm - revTotalDbm

        // Reflection coefficient Gamma = 10^(-RL / 20)
        val gamma = if (returnLossDb > 0.0) {
            10.0.pow(-returnLossDb / 20.0).coerceIn(0.0001, 0.9999)
        } else {
            0.9999
        }

        // VSWR = (1 + Gamma) / (1 - Gamma)
        val vswrRaw = (1.0 + gamma) / (1.0 - gamma)
        val vswr = (vswrRaw * 100.0).roundToInt() / 100.0

        val status = when {
            vswr <= 1.20 -> VswrStatus.EXCELLENT
            vswr <= 1.30 -> VswrStatus.NORMAL
            vswr <= 1.40 -> VswrStatus.ACCEPTABLE
            else -> VswrStatus.CRITICAL
        }

        return CalculationResult(
            incidentCouplerDb = incidentCouplerDb,
            revCouplerDb = revCouplerDb,
            fwdReadingMw = fwdReadingMw,
            fwdReadingDb = fwdReadingDb,
            revReadingDbAbs = revReadingDbAbs,
            fwdTotalDbm = (fwdTotalDbm * 100.0).roundToInt() / 100.0,
            revTotalDbm = (revTotalDbm * 100.0).roundToInt() / 100.0,
            returnLossDb = (returnLossDb * 100.0).roundToInt() / 100.0,
            reflectionCoefficient = (gamma * 10000.0).roundToInt() / 10000.0,
            vswr = vswr,
            peakPowerMw = peakPowerMw,
            peakPowerKw = peakPowerKw,
            status = status
        )
    }
}
