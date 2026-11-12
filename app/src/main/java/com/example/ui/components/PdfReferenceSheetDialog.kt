package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ChannelId
import com.example.data.SiteDataDefaults
import com.example.data.SiteId

@Composable
fun PdfReferenceSheetDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
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
                            color = Color(0xFFD32F2F).copy(alpha = 0.15f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(20.dp),
                                tint = Color(0xFFD32F2F)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Uploaded PDF Reference",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "SJU_STT_Peak_Power_VSWR-1-3.pdf",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
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

                // SJU Table
                SiteReferenceTable(
                    title = "SAN JUAN (SJU)",
                    siteId = SiteId.SJU,
                    chA = SiteDataDefaults.SJU_CH_A,
                    chB = SiteDataDefaults.SJU_CH_B
                )

                Spacer(modifier = Modifier.height(20.dp))

                // STT Table
                SiteReferenceTable(
                    title = "STT",
                    siteId = SiteId.STT,
                    chA = SiteDataDefaults.STT_CH_A,
                    chB = SiteDataDefaults.STT_CH_B
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun SiteReferenceTable(
    title: String,
    siteId: SiteId,
    chA: com.example.data.ChannelBaseline,
    chB: com.example.data.ChannelBaseline
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        // Table Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
        }

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.weight(2.0f)
            )
            Text(
                text = "CH A",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0077B6),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(0.9f)
            )
            Text(
                text = "NEW",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(0.9f)
            )
            Text(
                text = "CH B",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE76F51),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(0.9f)
            )
            Text(
                text = "NEW",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.weight(0.9f)
            )
        }

        // Data Rows
        PdfRow("Incident Power Coupler (dB)", "%.2f".format(chA.incidentCouplerDb), "%.2f".format(chB.incidentCouplerDb), tag = siteId.code)
        PdfRow("Rev Power Coupler (dB)", "%.2f".format(chA.revCouplerDb), "%.2f".format(chB.revCouplerDb), tag = siteId.code)
        PdfRow("Fwd Power Meter Reading (mW)", "%.2f".format(chA.fwdReadingMw), "%.2f".format(chB.fwdReadingMw))
        PdfRow("Fwd Power Meter Reading (dB)", "%.2f".format(chA.fwdReadingDb), "%.2f".format(chB.fwdReadingDb))
        PdfRow("Rev Power Meter Reading(dB) Abs", "%.2f".format(chA.revReadingDbAbs), "%.2f".format(chB.revReadingDbAbs))
        PdfRow("Peak Power Output", "%.2f".format(chA.peakPowerOutputMw), "%.2f".format(chB.peakPowerOutputMw), isHighlight = true)
        PdfRow("VSWR", "%.2f".format(chA.vswr), "%.2f".format(chB.vswr), isHighlight = true)
    }
}

@Composable
private fun PdfRow(
    label: String,
    chA: String,
    chB: String,
    tag: String? = null,
    isHighlight: Boolean = false
) {
    val textColor = if (isHighlight) Color(0xFF005082) else MaterialTheme.colorScheme.onSurface
    val textWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            .background(if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f) else Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.weight(2.0f)
        )
        Text(
            text = chA,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = textWeight,
                color = textColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.weight(0.9f)
        )
        Box(
            modifier = Modifier
                .weight(0.9f)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            )
        }
        Text(
            text = chB,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = textWeight,
                color = textColor,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.weight(0.9f)
        )
        Box(
            modifier = Modifier
                .weight(0.9f)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
