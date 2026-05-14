package com.kabaddiarena

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

@Composable
fun ResultScreen(
    teamAScore: Int,
    teamBScore: Int,
    mvpPlayer: String,
    matchSummary: String,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current
    val winner = when {
        teamAScore > teamBScore -> "Team A 🏆"
        teamBScore > teamAScore -> "Team B 🏆"
        else -> "Match Draw 🤝"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4A148C),
                        Color(0xFF7B1FA2),
                        Color(0xFFBA68C8)
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🏆 Match Result",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6A1B9A)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Winner : $winner",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Final Score : $teamAScore - $teamBScore",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "MVP : $mvpPlayer",
                    fontSize = 20.sp,
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        val bitmap = generatePerformanceCard(
                            context, winner, teamAScore, teamBScore, mvpPlayer, matchSummary
                        )
                        shareImage(context, bitmap)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE65100)
                    )
                ) {
                    Text(
                        text = "Share Card as IMAGE 🖼",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6A1B9A)
                    )
                ) {
                    Text(
                        text = "Back To Match",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun generatePerformanceCard(
    context: Context,
    winner: String,
    scoreA: Int,
    scoreB: Int,
    mvp: String,
    summary: String
): Bitmap {
    val width = 800
    val height = 1000
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    
    val paint = Paint()
    
    // Background
    paint.color = android.graphics.Color.WHITE
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    
    // Border
    paint.color = android.graphics.Color.parseColor("#6A1B9A")
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 20f
    canvas.drawRect(10f, 10f, width - 10f, height - 10f, paint)
    
    // Header
    paint.style = Paint.Style.FILL
    paint.textSize = 60f
    paint.isFakeBoldText = true
    canvas.drawText("KABADDI ARENA", 180f, 150f, paint)
    
    paint.textSize = 40f
    canvas.drawText("Performance Card", 230f, 210f, paint)
    
    // Lines
    paint.strokeWidth = 2f
    canvas.drawLine(100f, 250f, 700f, 250f, paint)
    
    // Content
    paint.color = android.graphics.Color.BLACK
    paint.textSize = 45f
    canvas.drawText("Winner: $winner", 100f, 350f, paint)
    
    paint.textSize = 40f
    canvas.drawText("Score: $scoreA - $scoreB", 100f, 450f, paint)
    
    paint.color = android.graphics.Color.parseColor("#E65100")
    paint.isFakeBoldText = true
    canvas.drawText("MVP: $mvp", 100f, 550f, paint)
    
    paint.color = android.graphics.Color.DKGRAY
    paint.isFakeBoldText = false
    paint.textSize = 30f
    
    // Summary wrapped text (simple approach)
    val summaryLines = summary.chunked(40)
    var y = 650f
    summaryLines.forEach { line ->
        canvas.drawText(line, 100f, y, paint)
        y += 40f
    }
    
    // Footer
    paint.color = android.graphics.Color.parseColor("#6A1B9A")
    paint.textSize = 25f
    canvas.drawText("Generated by KabaddiArena App", 220f, 950f, paint)
    
    return bitmap
}

private fun shareImage(context: Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "performance_card.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val contentUri = FileProvider.getUriForFile(
            context,
            "com.kabaddiarena.fileprovider",
            file
        )

        val intent = android.content.Intent(android.content.Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri)
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(android.content.Intent.createChooser(intent, "Share Performance Card"))
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
