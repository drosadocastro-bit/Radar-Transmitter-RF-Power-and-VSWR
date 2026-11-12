package com.example.ui.components

import kotlin.math.pow
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.CalculationResult
import com.example.data.ChannelId
import com.example.data.SiteId

@Composable
fun CalculationDetailDialog(
    site: SiteId,
    channel: ChannelId,
    result: CalculationResult,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Column {
                            Text(
                                text = "Formula Breakdown",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${site.code} - ${channel.title}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Forward Power
                FormulaSection(
                    title = "1. Forward Coupled Power",
                    formula = "P_fwd (dBm) = Incident Coupler (dB) + Fwd Reading (dB)",
                    substitution = "%.2f dB + %.2f dB = %.2f dBm".format(
                        result.incidentCouplerDb,
                        result.fwdReadingDb,
                        result.fwdTotalDbm
                    ),
                    resultText = "Total Forward Power = %.2f dBm".format(result.fwdTotalDbm)
                )

                // Section 2: Peak Power Output
                val avgW = 10.0.pow((result.fwdTotalDbm - 30.0) / 10.0)
                FormulaSection(
                    title = "2. Peak Power Output (MW)",
                    formula = "P_avg (W) = 10^((P_fwd - 30)/10)\nP_peak (MW) = P_avg / DutyFactor",
                    substitution = "P_avg = 10^((%.2f - 30)/10) = %.1f W\nP_peak = %.1f / %.2f = %.3f MW".format(
                        result.fwdTotalDbm,
                        avgW,
                        avgW,
                        site.dutyFactor,
                        result.peakPowerMw
                    ),
                    resultText = "Peak Power Output = %.2f MW (%.0f kW)".format(
                        result.peakPowerMw,
                        result.peakPowerKw
                    )
                )

                // Section 3: Reflected Power & Return Loss
                FormulaSection(
                    title = "3. Reflected Power & Return Loss",
                    formula = "P_rev (dBm) = Rev Coupler (dB) - |Rev Reading (dB)| + PadOffset\nReturn Loss RL (dB) = P_fwd (dBm) - P_rev (dBm)",
                    substitution = "P_rev = %.2f - %.2f + (%.2f) = %.2f dBm\nRL = %.2f - %.2f = %.2f dB".format(
                        result.revCouplerDb,
                        result.revReadingDbAbs,
                        site.padOffset,
                        result.revTotalDbm,
                        result.fwdTotalDbm,
                        result.revTotalDbm,
                        result.returnLossDb
                    ),
                    resultText = "Return Loss = %.2f dB".format(result.returnLossDb)
                )

                // Section 4: VSWR
                FormulaSection(
                    title = "4. Voltage Standing Wave Ratio (VSWR)",
                    formula = "Γ = 10^(-RL / 20)\nVSWR = (1 + Γ) / (1 - Γ)",
                    substitution = "Γ = 10^(-%.2f / 20) = %.4f\nVSWR = (1 + %.4f) / (1 - %.4f)".format(
                        result.returnLossDb,
                        result.reflectionCoefficient,
                        result.reflectionCoefficient,
                        result.reflectionCoefficient
                    ),
                    resultText = "VSWR = %.2f (%s)".format(result.vswr, result.status.label)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Done")
                    }
                }
            }
        }
    }
}

@Composable
private fun FormulaSection(
    title: String,
    formula: String,
    substitution: String,
    resultText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formula,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = substitution,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = resultText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
