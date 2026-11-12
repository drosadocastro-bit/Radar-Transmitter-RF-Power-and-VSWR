package com.example.ui.history

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MeasurementEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    measurements: List<MeasurementEntity>,
    onBack: () -> Unit,
    onDeleteMeasurement: (MeasurementEntity) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf<String?>("ALL") }
    var showClearConfirm by remember { mutableStateOf(false) }

    val filteredList = when (selectedFilter) {
        "SJU" -> measurements.filter { it.siteCode == "SJU" }
        "STT" -> measurements.filter { it.siteCode == "STT" }
        else -> measurements
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("Clear All Records?") },
            text = { Text("Are you sure you want to delete all saved measurement records? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearConfirm = false
                    }
                ) {
                    Text("Clear All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inspection History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                },
                actions = {
                    if (measurements.isNotEmpty()) {
                        IconButton(onClick = { shareReport(context, filteredList) }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share Report")
                        }
                        IconButton(onClick = { showClearConfirm = true }) {
                            Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear All")
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
                .padding(horizontal = 16.dp)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${measurements.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "SJU",
                    onClick = { selectedFilter = "SJU" },
                    label = { Text("SJU (${measurements.count { it.siteCode == "SJU" }})") }
                )
                FilterChip(
                    selected = selectedFilter == "STT",
                    onClick = { selectedFilter = "STT" },
                    label = { Text("STT (${measurements.count { it.siteCode == "STT" }})") }
                )
            }

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No saved inspection records",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Save measurements from the dashboard to log them here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            onDelete = { onDeleteMeasurement(item) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: MeasurementEntity,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US) }
    val formattedDate = remember(item.timestamp) { dateFormat.format(Date(item.timestamp)) }
    val isChannelA = item.channelCode.contains("A")
    val channelColor = if (isChannelA) Color(0xFF0077B6) else Color(0xFFE76F51)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = channelColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${item.siteCode} ${item.channelCode}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = channelColor
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Power & VSWR results
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Peak Power",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "%.2f MW (%.0f kW)".format(item.peakPowerMw, item.peakPowerKw),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "VSWR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "%.2f (RL: %.1f dB)".format(item.vswr, item.returnLossDb),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (item.vswr <= 1.25) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Details
            Text(
                text = "Couplers: Inc %.2f dB, Rev %.2f dB | Fwd: %.2f mW (%.2f dB) | Rev: %.2f dB".format(
                    item.incidentCouplerDb,
                    item.revCouplerDb,
                    item.fwdReadingMw,
                    item.fwdReadingDb,
                    item.revReadingDbAbs
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (item.technicianName.isNotBlank() || item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                if (item.technicianName.isNotBlank()) {
                    Text(
                        text = "Tech: ${item.technicianName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
                if (item.notes.isNotBlank()) {
                    Text(
                        text = "Notes: ${item.notes}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

private fun shareReport(context: Context, items: List<MeasurementEntity>) {
    val report = buildString {
        appendLine("=== FAA RADAR TRANSMITTER TELEMETRY REPORT ===")
        appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
        appendLine("Total Records: ${items.size}")
        appendLine("==============================================")
        appendLine()
        items.forEachIndexed { index, item ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date(item.timestamp))
            appendLine("#${index + 1}: ${item.siteCode} - ${item.channelCode} [$dateStr]")
            appendLine("  Peak Power: %.2f MW (%.0f kW)".format(item.peakPowerMw, item.peakPowerKw))
            appendLine("  VSWR: %.2f | Return Loss: %.2f dB".format(item.vswr, item.returnLossDb))
            appendLine("  Incident Coupler: %.2f dB | Rev Coupler: %.2f dB".format(item.incidentCouplerDb, item.revCouplerDb))
            appendLine("  Fwd Reading: %.2f mW (%.2f dB) | Rev Reading: %.2f dB".format(item.fwdReadingMw, item.fwdReadingDb, item.revReadingDbAbs))
            if (item.technicianName.isNotBlank()) appendLine("  Tech: ${item.technicianName}")
            if (item.notes.isNotBlank()) appendLine("  Notes: ${item.notes}")
            appendLine()
        }
    }

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "RF Peak Power and VSWR Inspection Report")
        putExtra(Intent.EXTRA_TEXT, report)
    }
    context.startActivity(Intent.createChooser(intent, "Share RF Report"))
}
