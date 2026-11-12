package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationResult
import com.example.data.ChannelBaseline
import com.example.data.ChannelId
import com.example.data.ChannelInputState
import com.example.data.MeasurementEntity
import com.example.data.MeasurementRepository
import com.example.data.RfCalculator
import com.example.data.SiteDataDefaults
import com.example.data.SiteId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    DASHBOARD,
    HISTORY
}

data class ActiveDialogState(
    val showCalculationDialog: Boolean = false,
    val calculationDialogSite: SiteId = SiteId.SJU,
    val calculationDialogChannel: ChannelId = ChannelId.CH_A,
    val calculationDialogResult: CalculationResult? = null,
    val showSaveDialog: Boolean = false,
    val saveDialogSite: SiteId = SiteId.SJU,
    val saveDialogChannel: ChannelId = ChannelId.CH_A,
    val saveDialogResult: CalculationResult? = null,
    val showPdfSheetDialog: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MeasurementRepository(AppDatabase.getInstance(application).measurementDao())

    val historyList: StateFlow<List<MeasurementEntity>> = repository.getAllMeasurements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedSite = MutableStateFlow(SiteId.SJU)
    val selectedSite: StateFlow<SiteId> = _selectedSite.asStateFlow()

    // SJU Inputs
    private val _sjuChAInput = MutableStateFlow(SiteDataDefaults.emptyInputFor(SiteDataDefaults.SJU_CH_A))
    val sjuChAInput: StateFlow<ChannelInputState> = _sjuChAInput.asStateFlow()

    private val _sjuChBInput = MutableStateFlow(SiteDataDefaults.emptyInputFor(SiteDataDefaults.SJU_CH_B))
    val sjuChBInput: StateFlow<ChannelInputState> = _sjuChBInput.asStateFlow()

    // STT Inputs
    private val _sttChAInput = MutableStateFlow(SiteDataDefaults.emptyInputFor(SiteDataDefaults.STT_CH_A))
    val sttChAInput: StateFlow<ChannelInputState> = _sttChAInput.asStateFlow()

    private val _sttChBInput = MutableStateFlow(SiteDataDefaults.emptyInputFor(SiteDataDefaults.STT_CH_B))
    val sttChBInput: StateFlow<ChannelInputState> = _sttChBInput.asStateFlow()

    private val _dialogState = MutableStateFlow(ActiveDialogState())
    val dialogState: StateFlow<ActiveDialogState> = _dialogState.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectSite(site: SiteId) {
        _selectedSite.value = site
    }

    fun updateChannelInput(site: SiteId, channel: ChannelId, newState: ChannelInputState) {
        when (site) {
            SiteId.SJU -> when (channel) {
                ChannelId.CH_A -> _sjuChAInput.value = newState
                ChannelId.CH_B -> _sjuChBInput.value = newState
            }
            SiteId.STT -> when (channel) {
                ChannelId.CH_A -> _sttChAInput.value = newState
                ChannelId.CH_B -> _sttChBInput.value = newState
            }
        }
    }

    fun copyBaselineToNew(site: SiteId, channel: ChannelId) {
        val baseline = SiteDataDefaults.getBaseline(site, channel)
        updateChannelInput(site, channel, SiteDataDefaults.populatedInputFromBaseline(baseline))
    }

    fun clearNewInput(site: SiteId, channel: ChannelId) {
        val baseline = SiteDataDefaults.getBaseline(site, channel)
        updateChannelInput(site, channel, SiteDataDefaults.emptyInputFor(baseline))
    }

    fun computeResult(site: SiteId, channel: ChannelId, input: ChannelInputState): CalculationResult? {
        val baseline = SiteDataDefaults.getBaseline(site, channel)
        val incCoupler = input.incidentCouplerInput.toDoubleOrNull() ?: baseline.incidentCouplerDb
        val revCoupler = input.revCouplerInput.toDoubleOrNull() ?: baseline.revCouplerDb
        val fwdDb = input.fwdReadingDbInput.toDoubleOrNull()
        val fwdMw = input.fwdReadingMwInput.toDoubleOrNull()
        val revDbAbs = input.revReadingDbAbsInput.toDoubleOrNull()

        // If no user readings are entered, return null to show baseline values
        if (fwdDb == null && fwdMw == null && revDbAbs == null) {
            return null
        }

        val effectiveFwdDb = fwdDb ?: (if (fwdMw != null) RfCalculator.mwToDb(fwdMw) else baseline.fwdReadingDb)
        val effectiveFwdMw = fwdMw ?: RfCalculator.dbToMw(effectiveFwdDb)
        val effectiveRevDbAbs = revDbAbs ?: baseline.revReadingDbAbs

        return RfCalculator.calculate(
            incidentCouplerDb = incCoupler,
            revCouplerDb = revCoupler,
            fwdReadingDb = effectiveFwdDb,
            fwdReadingMw = effectiveFwdMw,
            revReadingDbAbs = effectiveRevDbAbs,
            dutyFactor = site.dutyFactor,
            padOffset = site.padOffset
        )
    }

    fun getBaselineCalculation(site: SiteId, channel: ChannelId): CalculationResult {
        val baseline = SiteDataDefaults.getBaseline(site, channel)
        return RfCalculator.calculate(
            incidentCouplerDb = baseline.incidentCouplerDb,
            revCouplerDb = baseline.revCouplerDb,
            fwdReadingDb = baseline.fwdReadingDb,
            fwdReadingMw = baseline.fwdReadingMw,
            revReadingDbAbs = baseline.revReadingDbAbs,
            dutyFactor = site.dutyFactor,
            padOffset = site.padOffset
        )
    }

    fun openCalculationDialog(site: SiteId, channel: ChannelId, result: CalculationResult) {
        _dialogState.value = _dialogState.value.copy(
            showCalculationDialog = true,
            calculationDialogSite = site,
            calculationDialogChannel = channel,
            calculationDialogResult = result
        )
    }

    fun closeCalculationDialog() {
        _dialogState.value = _dialogState.value.copy(showCalculationDialog = false)
    }

    fun openSaveDialog(site: SiteId, channel: ChannelId, result: CalculationResult) {
        _dialogState.value = _dialogState.value.copy(
            showSaveDialog = true,
            saveDialogSite = site,
            saveDialogChannel = channel,
            saveDialogResult = result
        )
    }

    fun closeSaveDialog() {
        _dialogState.value = _dialogState.value.copy(showSaveDialog = false)
    }

    fun openPdfSheetDialog() {
        _dialogState.value = _dialogState.value.copy(showPdfSheetDialog = true)
    }

    fun closePdfSheetDialog() {
        _dialogState.value = _dialogState.value.copy(showPdfSheetDialog = false)
    }

    fun saveMeasurement(
        site: SiteId,
        channel: ChannelId,
        result: CalculationResult,
        techName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val entity = MeasurementEntity(
                siteCode = site.code,
                channelCode = channel.code,
                timestamp = System.currentTimeMillis(),
                incidentCouplerDb = result.incidentCouplerDb,
                revCouplerDb = result.revCouplerDb,
                fwdReadingMw = result.fwdReadingMw,
                fwdReadingDb = result.fwdReadingDb,
                revReadingDbAbs = result.revReadingDbAbs,
                peakPowerMw = result.peakPowerMw,
                peakPowerKw = result.peakPowerKw,
                vswr = result.vswr,
                returnLossDb = result.returnLossDb,
                statusLabel = result.status.label,
                technicianName = techName,
                notes = notes
            )
            repository.saveMeasurement(entity)
        }
    }

    fun deleteMeasurement(entity: MeasurementEntity) {
        viewModelScope.launch {
            repository.deleteMeasurement(entity)
        }
    }

    fun clearAllMeasurements() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
