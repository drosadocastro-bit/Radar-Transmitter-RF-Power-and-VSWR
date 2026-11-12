package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AppScreen
import com.example.MainViewModel
import com.example.data.ChannelId
import com.example.data.SiteDataDefaults
import com.example.data.SiteId
import com.example.ui.components.CalculationDetailDialog
import com.example.ui.components.ChannelCard
import com.example.ui.components.PdfReferenceSheetDialog
import com.example.ui.components.SaveMeasurementDialog
import com.example.ui.components.SiteToggle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSite by viewModel.selectedSite.collectAsState()
    val sjuChAInput by viewModel.sjuChAInput.collectAsState()
    val sjuChBInput by viewModel.sjuChBInput.collectAsState()
    val sttChAInput by viewModel.sttChAInput.collectAsState()
    val sttChBInput by viewModel.sttChBInput.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

    var channelFilterIndex by remember { mutableIntStateOf(0) } // 0: Both Channels, 1: CH A, 2: CH B

    // Active channel inputs for current selected site
    val chAInput = if (selectedSite == SiteId.SJU) sjuChAInput else sttChAInput
    val chBInput = if (selectedSite == SiteId.SJU) sjuChBInput else sttChBInput

    // Active baseline objects
    val chABaseline = SiteDataDefaults.getBaseline(selectedSite, ChannelId.CH_A)
    val chBBaseline = SiteDataDefaults.getBaseline(selectedSite, ChannelId.CH_B)

    // Live computed results
    val chAResult = viewModel.computeResult(selectedSite, ChannelId.CH_A, chAInput)
    val chBResult = viewModel.computeResult(selectedSite, ChannelId.CH_B, chBInput)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RF Power & VSWR",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "SJU / STT Transmitter Telemetry",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openPdfSheetDialog() },
                        modifier = Modifier.testTag("pdf_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Reference Sheet",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                        modifier = Modifier.testTag("history_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (historyList.isNotEmpty()) {
                                    Badge {
                                        Text(text = historyList.size.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Inspection History"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Site Toggle (SJU vs STT)
            SiteToggle(
                selectedSite = selectedSite,
                onSiteSelected = { newSite -> viewModel.selectSite(newSite) }
            )

            // Channel Filter Selector Tabs
            ScrollableTabRow(
                selectedTabIndex = channelFilterIndex,
                edgePadding = 0.dp,
                divider = {},
                containerColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(2.dp)
            ) {
                Tab(
                    selected = channelFilterIndex == 0,
                    onClick = { channelFilterIndex = 0 },
                    text = { Text("Both Channels (A & B)", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = channelFilterIndex == 1,
                    onClick = { channelFilterIndex = 1 },
                    text = { Text("Channel A Only", style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = channelFilterIndex == 2,
                    onClick = { channelFilterIndex = 2 },
                    text = { Text("Channel B Only", style = MaterialTheme.typography.labelMedium) }
                )
            }

            // Animated Site & Channel Container
            AnimatedContent(
                targetState = selectedSite,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "site_channel_content"
            ) { currentSite ->
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Channel A
                    if (channelFilterIndex == 0 || channelFilterIndex == 1) {
                        ChannelCard(
                            site = currentSite,
                            baseline = chABaseline,
                            inputState = chAInput,
                            calculationResult = chAResult,
                            onInputChange = { newState ->
                                viewModel.updateChannelInput(currentSite, ChannelId.CH_A, newState)
                            },
                            onSaveLog = {
                                val result = chAResult ?: viewModel.getBaselineCalculation(currentSite, ChannelId.CH_A)
                                viewModel.openSaveDialog(currentSite, ChannelId.CH_A, result)
                            },
                            onShowFormulas = {
                                val result = chAResult ?: viewModel.getBaselineCalculation(currentSite, ChannelId.CH_A)
                                viewModel.openCalculationDialog(currentSite, ChannelId.CH_A, result)
                            },
                            onCopyBaseline = {
                                viewModel.copyBaselineToNew(currentSite, ChannelId.CH_A)
                            },
                            onClearNew = {
                                viewModel.clearNewInput(currentSite, ChannelId.CH_A)
                            }
                        )
                    }

                    // Channel B
                    if (channelFilterIndex == 0 || channelFilterIndex == 2) {
                        ChannelCard(
                            site = currentSite,
                            baseline = chBBaseline,
                            inputState = chBInput,
                            calculationResult = chBResult,
                            onInputChange = { newState ->
                                viewModel.updateChannelInput(currentSite, ChannelId.CH_B, newState)
                            },
                            onSaveLog = {
                                val result = chBResult ?: viewModel.getBaselineCalculation(currentSite, ChannelId.CH_B)
                                viewModel.openSaveDialog(currentSite, ChannelId.CH_B, result)
                            },
                            onShowFormulas = {
                                val result = chBResult ?: viewModel.getBaselineCalculation(currentSite, ChannelId.CH_B)
                                viewModel.openCalculationDialog(currentSite, ChannelId.CH_B, result)
                            },
                            onCopyBaseline = {
                                viewModel.copyBaselineToNew(currentSite, ChannelId.CH_B)
                            },
                            onClearNew = {
                                viewModel.clearNewInput(currentSite, ChannelId.CH_B)
                            }
                        )
                    }
                }
            }

            // Reference Info Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "FAA Technical Reference Standard",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Calibrated for directional couplers & power sensors at San Juan (SJU) & St. Thomas (STT). Target VSWR <= 1.25, Peak Power 0.70 - 0.90 MW.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialogs
    if (dialogState.showCalculationDialog && dialogState.calculationDialogResult != null) {
        CalculationDetailDialog(
            site = dialogState.calculationDialogSite,
            channel = dialogState.calculationDialogChannel,
            result = dialogState.calculationDialogResult!!,
            onDismiss = { viewModel.closeCalculationDialog() }
        )
    }

    if (dialogState.showSaveDialog && dialogState.saveDialogResult != null) {
        SaveMeasurementDialog(
            site = dialogState.saveDialogSite,
            channel = dialogState.saveDialogChannel,
            result = dialogState.saveDialogResult!!,
            onSave = { techName, notes ->
                viewModel.saveMeasurement(
                    site = dialogState.saveDialogSite,
                    channel = dialogState.saveDialogChannel,
                    result = dialogState.saveDialogResult!!,
                    techName = techName,
                    notes = notes
                )
            },
            onDismiss = { viewModel.closeSaveDialog() }
        )
    }

    if (dialogState.showPdfSheetDialog) {
        PdfReferenceSheetDialog(
            onDismiss = { viewModel.closePdfSheetDialog() }
        )
    }
}
