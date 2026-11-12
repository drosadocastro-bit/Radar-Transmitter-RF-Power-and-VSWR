package com.example

import androidx.lifecycle.ViewModel
import com.example.data.CalculationResult
import com.example.data.ChannelBaseline
import com.example.data.ChannelId
import com.example.data.RfCalculator
import com.example.data.SiteDataDefaults
import com.example.data.SiteId
import com.example.data.VswrStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * ViewModel dedicated to handling the mathematical formulas for calculating
 * Peak Power Output and VSWR based on input forward and reflected power values.
 *
 * Formulas:
 * 1. Forward Power:
 *    P_fwd (dBm) = IncidentCoupler (dB) + FwdReading (dB)
 *    P_avg (W) = 10^((P_fwd - 30) / 10)
 *    P_peak (MW) = P_avg / DutyFactor
 *    P_peak (kW) = P_peak (MW) * 1000
 *
 * 2. Reflected Power:
 *    P_rev (dBm) = RevCoupler (dB) - |RevReading (dB)| + PadOffset
 *
 * 3. Return Loss & VSWR:
 *    Return Loss RL (dB) = P_fwd (dBm) - P_rev (dBm)
 *    Reflection Coefficient Γ = 10^(-RL / 20)
 *    VSWR = (1 + Γ) / (1 - Γ)
 */
class RfCalculatorViewModel : ViewModel() {

    // Target Facility and Channel
    private val _selectedSite = MutableStateFlow(SiteId.SJU)
    val selectedSite: StateFlow<SiteId> = _selectedSite.asStateFlow()

    private val _selectedChannel = MutableStateFlow(ChannelId.CH_A)
    val selectedChannel: StateFlow<ChannelId> = _selectedChannel.asStateFlow()

    // Input States
    private val _incidentCouplerDb = MutableStateFlow("54.18")
    val incidentCouplerDb: StateFlow<String> = _incidentCouplerDb.asStateFlow()

    private val _revCouplerDb = MutableStateFlow("44.87")
    val revCouplerDb: StateFlow<String> = _revCouplerDb.asStateFlow()

    private val _fwdReadingMw = MutableStateFlow("1.81")
    val fwdReadingMw: StateFlow<String> = _fwdReadingMw.asStateFlow()

    private val _fwdReadingDb = MutableStateFlow("2.55")
    val fwdReadingDb: StateFlow<String> = _fwdReadingDb.asStateFlow()

    private val _revReadingDbAbs = MutableStateFlow("9.55")
    val revReadingDbAbs: StateFlow<String> = _revReadingDbAbs.asStateFlow()

    private val _dutyFactor = MutableStateFlow(SiteId.SJU.dutyFactor)
    val dutyFactor: StateFlow<Double> = _dutyFactor.asStateFlow()

    private val _padOffset = MutableStateFlow(SiteId.SJU.padOffset)
    val padOffset: StateFlow<Double> = _padOffset.asStateFlow()

    // Live Calculation Result
    private val _calculationResult = MutableStateFlow(
        computeCalculation(
            incidentDb = 54.18,
            revCouplerDb = 44.87,
            fwdDb = 2.55,
            fwdMw = 1.81,
            revDbAbs = 9.55,
            duty = SiteId.SJU.dutyFactor,
            pad = SiteId.SJU.padOffset
        )
    )
    val calculationResult: StateFlow<CalculationResult> = _calculationResult.asStateFlow()

    init {
        recalculate()
    }

    /**
     * Updates the site and applies site-specific calibration factors (dutyFactor and padOffset).
     */
    fun setSite(site: SiteId) {
        _selectedSite.value = site
        _dutyFactor.value = site.dutyFactor
        _padOffset.value = site.padOffset
        recalculate()
    }

    fun setChannel(channel: ChannelId) {
        _selectedChannel.value = channel
        recalculate()
    }

    /**
     * Loads calibrated baseline values directly from the PDF document for the given site and channel.
     */
    fun loadBaseline(site: SiteId = _selectedSite.value, channel: ChannelId = _selectedChannel.value) {
        val baseline: ChannelBaseline = SiteDataDefaults.getBaseline(site, channel)
        _selectedSite.value = site
        _selectedChannel.value = channel
        _incidentCouplerDb.value = baseline.incidentCouplerDb.toString()
        _revCouplerDb.value = baseline.revCouplerDb.toString()
        _fwdReadingMw.value = baseline.fwdReadingMw.toString()
        _fwdReadingDb.value = baseline.fwdReadingDb.toString()
        _revReadingDbAbs.value = baseline.revReadingDbAbs.toString()
        _dutyFactor.value = site.dutyFactor
        _padOffset.value = site.padOffset
        recalculate()
    }

    /**
     * Sets incident coupler attenuation in dB.
     */
    fun setIncidentCouplerDb(value: String) {
        _incidentCouplerDb.value = value
        recalculate()
    }

    /**
     * Sets reverse coupler attenuation in dB.
     */
    fun setRevCouplerDb(value: String) {
        _revCouplerDb.value = value
        recalculate()
    }

    /**
     * Sets forward power in mW, automatically converting to dBm:
     * dB = 10 * log10(mW)
     */
    fun setFwdPowerMw(value: String) {
        _fwdReadingMw.value = value
        val mw = value.toDoubleOrNull()
        if (mw != null && mw > 0.0) {
            val db = 10.0 * log10(mw)
            _fwdReadingDb.value = "%.2f".format(db)
        }
        recalculate()
    }

    /**
     * Sets forward power in dBm, automatically converting to mW:
     * mW = 10^(dB / 10)
     */
    fun setFwdPowerDb(value: String) {
        _fwdReadingDb.value = value
        val db = value.toDoubleOrNull()
        if (db != null) {
            val mw = 10.0.pow(db / 10.0)
            _fwdReadingMw.value = "%.2f".format(mw)
        }
        recalculate()
    }

    /**
     * Sets reverse power meter reading absolute value in dB.
     */
    fun setRevPowerReadingDbAbs(value: String) {
        _revReadingDbAbs.value = value
        recalculate()
    }

    /**
     * Sets custom duty cycle factor (default: ~620 for SJU, ~626 for STT).
     */
    fun setDutyFactor(factor: Double) {
        _dutyFactor.value = factor
        recalculate()
    }

    /**
     * Sets custom pad offset in dB.
     */
    fun setPadOffset(offset: Double) {
        _padOffset.value = offset
        recalculate()
    }

    /**
     * Triggers mathematical recalculation of Peak Power Output and VSWR
     * based on current input values.
     */
    private fun recalculate() {
        val incDb = _incidentCouplerDb.value.toDoubleOrNull() ?: 54.00
        val revCplDb = _revCouplerDb.value.toDoubleOrNull() ?: 45.00
        val fwdDb = _fwdReadingDb.value.toDoubleOrNull() ?: 2.50
        val fwdMw = _fwdReadingMw.value.toDoubleOrNull() ?: RfCalculator.dbToMw(fwdDb)
        val revDbAbs = _revReadingDbAbs.value.toDoubleOrNull() ?: 9.55

        _calculationResult.value = computeCalculation(
            incidentDb = incDb,
            revCouplerDb = revCplDb,
            fwdDb = fwdDb,
            fwdMw = fwdMw,
            revDbAbs = revDbAbs,
            duty = _dutyFactor.value,
            pad = _padOffset.value
        )
    }

    /**
     * Pure mathematical calculation function.
     */
    fun computeCalculation(
        incidentDb: Double,
        revCouplerDb: Double,
        fwdDb: Double,
        fwdMw: Double,
        revDbAbs: Double,
        duty: Double,
        pad: Double
    ): CalculationResult {
        // Step 1: Forward power in dBm
        val fwdTotalDbm = incidentDb + fwdDb

        // Step 2: Average power in Watts
        val avgWatts = 10.0.pow((fwdTotalDbm - 30.0) / 10.0)

        // Step 3: Peak power in Megawatts & Kilowatts
        val peakMwRaw = if (duty > 0.0) avgWatts / duty else 0.0
        val peakMw = (peakMwRaw * 100.0).roundToInt() / 100.0
        val peakKw = (peakMwRaw * 1000.0 * 10.0).roundToInt() / 10.0

        // Step 4: Reflected power in dBm
        val revTotalDbm = revCouplerDb - revDbAbs + pad

        // Step 5: Return Loss (RL) in dB
        val returnLossDb = fwdTotalDbm - revTotalDbm

        // Step 6: Reflection Coefficient Gamma (Γ)
        val gamma = if (returnLossDb > 0.0) {
            10.0.pow(-returnLossDb / 20.0).coerceIn(0.0001, 0.9999)
        } else {
            0.9999
        }

        // Step 7: VSWR
        val vswrRaw = (1.0 + gamma) / (1.0 - gamma)
        val vswr = (vswrRaw * 100.0).roundToInt() / 100.0

        val status = when {
            vswr <= 1.20 -> VswrStatus.EXCELLENT
            vswr <= 1.30 -> VswrStatus.NORMAL
            vswr <= 1.40 -> VswrStatus.ACCEPTABLE
            else -> VswrStatus.CRITICAL
        }

        return CalculationResult(
            incidentCouplerDb = incidentDb,
            revCouplerDb = revCouplerDb,
            fwdReadingMw = fwdMw,
            fwdReadingDb = fwdDb,
            revReadingDbAbs = revDbAbs,
            fwdTotalDbm = (fwdTotalDbm * 100.0).roundToInt() / 100.0,
            revTotalDbm = (revTotalDbm * 100.0).roundToInt() / 100.0,
            returnLossDb = (returnLossDb * 100.0).roundToInt() / 100.0,
            reflectionCoefficient = (gamma * 10000.0).roundToInt() / 10000.0,
            vswr = vswr,
            peakPowerMw = peakMw,
            peakPowerKw = peakKw,
            status = status
        )
    }
}
