package com.example.ui.components

import kotlin.math.log10
import kotlin.math.pow
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CalculationResult
import com.example.data.ChannelBaseline
import com.example.data.ChannelId
import com.example.data.ChannelInputState
import com.example.data.SiteId
import com.example.ui.theme.ChannelAColor
import com.example.ui.theme.ChannelBColor

@Composable
fun ChannelCard(
    site: SiteId,
    baseline: ChannelBaseline,
    inputState: ChannelInputState,
    calculationResult: CalculationResult?,
    onInputChange: (ChannelInputState) -> Unit,
    onSaveLog: () -> Unit,
    onShowFormulas: () -> Unit,
    onCopyBaseline: () -> Unit,
    onClearNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val channelColor = if (baseline.channelId == ChannelId.CH_A) ChannelAColor else ChannelBColor
    var showGauge by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("channel_card_${baseline.channelId.code.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Channel Tag + Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = channelColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = baseline.channelId.code,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = channelColor
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${site.code} - ${baseline.channelId.title}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Coupled Transmitter Telemetry",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onCopyBaseline,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Baseline to New",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onClearNew,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Clear Inputs",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Measurement Table: Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PARAMETER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1.8f)
                )
                Text(
                    text = "BASELINE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = channelColor
                    ),
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "NEW ENTRY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1.3f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 1: Incident Power Coupler (dB)
            TableRowInput(
                label = "Incident Coupler (dB)",
                baselineValue = "%.2f".format(baseline.incidentCouplerDb),
                inputValue = inputState.incidentCouplerInput,
                onValueChange = { onInputChange(inputState.copy(incidentCouplerInput = it)) },
                testTag = "input_incident_coupler"
            )

            // Row 2: Rev Power Coupler (dB)
            TableRowInput(
                label = "Rev Coupler (dB)",
                baselineValue = "%.2f".format(baseline.revCouplerDb),
                inputValue = inputState.revCouplerInput,
                onValueChange = { onInputChange(inputState.copy(revCouplerInput = it)) },
                testTag = "input_rev_coupler"
            )

            // Row 3: Fwd Power Meter Reading (mW)
            TableRowInput(
                label = "Fwd Meter (mW)",
                baselineValue = "%.2f".format(baseline.fwdReadingMw),
                inputValue = inputState.fwdReadingMwInput,
                onValueChange = { mwStr ->
                    // Auto-sync to dB
                    val mw = mwStr.toDoubleOrNull()
                    val newDb = if (mw != null && mw > 0) {
                        "%.2f".format(10.0 * kotlin.math.log10(mw))
                    } else ""
                    onInputChange(inputState.copy(fwdReadingMwInput = mwStr, fwdReadingDbInput = newDb))
                },
                testTag = "input_fwd_mw"
            )

            // Row 4: Fwd Power Meter Reading (dB)
            TableRowInput(
                label = "Fwd Meter (dB)",
                baselineValue = "%.2f".format(baseline.fwdReadingDb),
                inputValue = inputState.fwdReadingDbInput,
                onValueChange = { dbStr ->
                    // Auto-sync to mW
                    val db = dbStr.toDoubleOrNull()
                    val newMw = if (db != null) {
                        "%.2f".format(10.0.pow(db / 10.0))
                    } else ""
                    onInputChange(inputState.copy(fwdReadingDbInput = dbStr, fwdReadingMwInput = newMw))
                },
                testTag = "input_fwd_db"
            )

            // Row 5: Rev Power Meter Reading (dB) Absolute Value
            TableRowInput(
                label = "Rev Meter (|dB|)",
                baselineValue = "%.2f".format(baseline.revReadingDbAbs),
                inputValue = inputState.revReadingDbAbsInput,
                onValueChange = { onInputChange(inputState.copy(revReadingDbAbsInput = it)) },
                testTag = "input_rev_db_abs"
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Calculated Output Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Peak Power Card
                val newPeak = calculationResult?.peakPowerMw
                val baselinePeak = baseline.peakPowerOutputMw
                val peakDelta = if (newPeak != null) newPeak - baselinePeak else null

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Peak Power Output",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = if (newPeak != null) "%.2f MW".format(newPeak) else "%.2f MW".format(baselinePeak),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = if (newPeak != null) "(%.0f kW)".format(calculationResult.peakPowerKw) else "(%.0f kW)".format(baselinePeak * 1000.0),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        if (peakDelta != null && kotlin.math.abs(peakDelta) > 0.001) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val deltaSign = if (peakDelta >= 0) "+" else ""
                            val pct = (peakDelta / baselinePeak) * 100.0
                            Text(
                                text = "Δ $deltaSign%.2f MW ($deltaSign%.1f%%)".format(peakDelta, pct),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (kotlin.math.abs(pct) <= 5.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            )
                        } else {
                            Text(
                                text = "Baseline: %.2f MW".format(baselinePeak),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // VSWR Card
                val newVswr = calculationResult?.vswr
                val baselineVswr = baseline.vswr
                val vswrDelta = if (newVswr != null) newVswr - baselineVswr else null
                val statusColor = calculationResult?.status?.let { Color(it.colorHex) } ?: MaterialTheme.colorScheme.primary

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.12f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "VSWR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (newVswr != null) "%.2f".format(newVswr) else "%.2f".format(baselineVswr),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace,
                                color = statusColor
                            )
                        )
                        Text(
                            text = calculationResult?.status?.label ?: "Baseline",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = statusColor
                            )
                        )

                        if (vswrDelta != null && kotlin.math.abs(vswrDelta) > 0.005) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val deltaSign = if (vswrDelta >= 0) "+" else ""
                            Text(
                                text = "Δ $deltaSign%.2f vs baseline".format(vswrDelta),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (vswrDelta <= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            )
                        } else {
                            Text(
                                text = "Baseline: %.2f".format(baselineVswr),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Optional VSWR Gauge expansion
            if (calculationResult != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showGauge = !showGauge },
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showGauge) "Hide Gauge" else "Show Gauge",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                AnimatedVisibility(visible = showGauge) {
                    VswrGauge(
                        vswr = calculationResult.vswr,
                        returnLossDb = calculationResult.returnLossDb,
                        status = calculationResult.status
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Save Log & Math Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onShowFormulas,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Math Breakdown", style = MaterialTheme.typography.labelMedium)
                }

                Button(
                    onClick = onSaveLog,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = channelColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Save Log", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
private fun TableRowInput(
    label: String,
    baselineValue: String,
    inputValue: String,
    onValueChange: (String) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.weight(1.8f)
        )

        Text(
            text = baselineValue,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.weight(1.1f)
        )

        OutlinedTextField(
            value = inputValue,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = baselineValue,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    )
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .weight(1.3f)
                .height(48.dp)
                .testTag(testTag)
        )
    }
}
