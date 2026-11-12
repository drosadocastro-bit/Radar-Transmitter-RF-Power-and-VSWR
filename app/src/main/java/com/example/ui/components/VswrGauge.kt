package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VswrStatus
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VswrGauge(
    vswr: Double,
    returnLossDb: Double,
    status: VswrStatus,
    modifier: Modifier = Modifier
) {
    // VSWR range from 1.0 to 2.0 (clamped for visual needle)
    val clampedVswr = vswr.toFloat().coerceIn(1.0f, 2.0f)
    val targetFraction = (clampedVswr - 1.0f) / 1.0f // 0.0 at 1.0, 1.0 at 2.0

    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(durationMillis = 600),
        label = "vswr_needle"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 200.dp, height = 110.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(width = 190.dp, height = 100.dp)) {
                val strokeWidth = 14.dp.toPx()
                val arcSize = Size(size.width - strokeWidth, (size.height * 2) - strokeWidth)
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                // Background arc (180 degrees from 180 to 360)
                drawArc(
                    color = Color.LightGray.copy(alpha = 0.25f),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Colored zone gradient: Green (1.0 - 1.2), Blue (1.2 - 1.3), Amber (1.3 - 1.4), Red (1.4 - 2.0)
                // 1.0 to 1.2 is 20% of sweep = 36 deg
                // 1.2 to 1.3 is 10% = 18 deg
                // 1.3 to 1.4 is 10% = 18 deg
                // 1.4 to 2.0 is 60% = 108 deg
                drawArc(
                    brush = Brush.sweepGradient(
                        0.5f to Color(0xFF2E7D32),
                        0.6f to Color(0xFF0077B6),
                        0.7f to Color(0xFFF57C00),
                        0.9f to Color(0xFFD32F2F),
                        1.0f to Color(0xFFD32F2F),
                        center = Offset(size.width / 2, size.height)
                    ),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Needle indicator
                val needleAngleRad = Math.toRadians((180.0 + (animatedFraction * 180.0)))
                val needleRadius = (size.width / 2) - strokeWidth - 4.dp.toPx()
                val centerX = size.width / 2
                val centerY = size.height

                val endX = centerX + (needleRadius * cos(needleAngleRad)).toFloat()
                val endY = centerY + (needleRadius * sin(needleAngleRad)).toFloat()

                // Draw needle line
                drawLine(
                    color = Color(status.colorHex),
                    start = Offset(centerX, centerY),
                    end = Offset(endX, endY),
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Draw center pivot hub
                drawCircle(
                    color = Color(status.colorHex),
                    radius = 7.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(centerX, centerY)
                )
            }

            // Digital readout in center of arc
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "%.2f".format(vswr),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(status.colorHex)
                    )
                )
                Text(
                    text = "VSWR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        // Return Loss and Status indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1.00 (Perfect)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Text(
                text = "Return Loss: %.1f dB".format(returnLossDb),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "2.00+ (High)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
