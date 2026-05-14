package com.kabaddiarena

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CourtScreen(onBackClicked: () -> Unit) {
    var raidPoints by remember { mutableStateOf(listOf<Offset>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20)) // Darker green background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🏟 Interactive Heatmap",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Mark where raids happened on the court",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Kabaddi Court Drawing with Heatmap effect
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2E7D32))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            raidPoints = raidPoints + offset
                        }
                    }
            ) {
                val w = size.width
                val h = size.height
                val lineColor = Color.White

                // Mid Line
                drawLine(
                    color = lineColor,
                    start = Offset(0f, h / 2),
                    end = Offset(w, h / 2),
                    strokeWidth = 8f
                )

                // Baulk Lines
                drawLine(
                    color = lineColor,
                    start = Offset(0f, h / 2 - h * 0.18f),
                    end = Offset(w, h / 2 - h * 0.18f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = lineColor,
                    start = Offset(0f, h / 2 + h * 0.18f),
                    end = Offset(w, h / 2 + h * 0.18f),
                    strokeWidth = 4f
                )

                // Bonus Lines
                drawLine(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    start = Offset(0f, h / 2 - h * 0.30f),
                    end = Offset(w, h / 2 - h * 0.30f),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color.Yellow.copy(alpha = 0.8f),
                    start = Offset(0f, h / 2 + h * 0.30f),
                    end = Offset(w, h / 2 + h * 0.30f),
                    strokeWidth = 4f
                )
                
                // End Lines
                drawRect(color = lineColor, style = Stroke(width = 5f))

                // Draw Heatmap circles (Red centers with fading orange/yellow glows)
                raidPoints.forEach { point ->
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.Red, Color.Yellow.copy(alpha = 0.3f), Color.Transparent),
                            center = point,
                            radius = 60f
                        ),
                        center = point,
                        radius = 60f
                    )
                    drawCircle(
                        color = Color.Red,
                        center = point,
                        radius = 10f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { raidPoints = emptyList() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Reset Heatmap 🔄")
            }
            Button(
                onClick = onBackClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
            ) {
                Text("Done ✅")
            }
        }
    }
}
