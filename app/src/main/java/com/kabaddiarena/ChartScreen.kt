package com.kabaddiarena

import android.graphics.Color as AndroidColor
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*

@Composable
fun ChartScreen(
    raidSuccess: Int,
    tacklePoints: Int,
    teamAScore: Int,
    teamBScore: Int,
    onBackClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📈 Charts Dashboard",
            fontSize = 32.sp,
            color = Color(0xFF6A1B9A)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ================= BAR CHART =================
        Text(text = "Bar Chart: Match Score", fontSize = 18.sp, color = Color.Gray)
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            factory = { context ->
                BarChart(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    description.isEnabled = false
                    val entries = listOf(
                        BarEntry(1f, teamAScore.toFloat()),
                        BarEntry(2f, teamBScore.toFloat())
                    )
                    val dataSet = BarDataSet(entries, "Team Scores").apply {
                        colors = listOf(AndroidColor.RED, AndroidColor.BLUE)
                    }
                    data = BarData(dataSet)
                    invalidate()
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        // ================= LINE CHART =================
        Text(text = "Line Graph: Points Trend", fontSize = 18.sp, color = Color.Gray)
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            factory = { context ->
                LineChart(context).apply {
                    layoutParams = android.view.ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    description.isEnabled = false
                    val entries = listOf(
                        Entry(0f, 0f),
                        Entry(1f, raidSuccess.toFloat()),
                        Entry(2f, (raidSuccess + tacklePoints).toFloat())
                    )
                    val dataSet = LineDataSet(entries, "Total Points").apply {
                        color = AndroidColor.MAGENTA
                        setCircleColor(AndroidColor.BLACK)
                    }
                    data = LineData(dataSet)
                    invalidate()
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onBackClicked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Back",
                fontSize = 20.sp
            )
        }
    }
}
