package com.kabaddiarena

import android.graphics.Color
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun PieChartScreen(
    raidSuccess: Int,
    tacklePoints: Int,
    onBackClicked: () -> Unit
) {

    BackHandler {
        onBackClicked()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        AndroidView(

            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),

            factory = { context ->

                PieChart(context).apply {

                    layoutParams =
                        android.view.ViewGroup.LayoutParams(
                            MATCH_PARENT,
                            MATCH_PARENT
                        )

                    description.isEnabled = false

                    val entries = ArrayList<PieEntry>()

                    entries.add(
                        PieEntry(
                            raidSuccess.toFloat(),
                            "Raid Success"
                        )
                    )

                    entries.add(
                        PieEntry(
                            tacklePoints.toFloat(),
                            "Tackle Points"
                        )
                    )

                    val dataSet =
                        PieDataSet(
                            entries,
                            "Kabaddi Stats"
                        )

                    dataSet.colors = listOf(
                        Color.GREEN,
                        Color.BLUE
                    )

                    val data = PieData(dataSet)

                    this.data = data

                    invalidate()
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

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
