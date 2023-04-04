package com.smarttoolfactory.tutorial1_1basics.chapter6_graphics

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.chart.ChartData
import com.smarttoolfactory.tutorial1_1basics.chapter6_graphics.chart.PieChart
import com.smarttoolfactory.tutorial1_1basics.ui.*

@Composable
fun Tutorial6_15Screen() {
    TutorialContent()
}

@Preview
@Composable
private fun TutorialContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        val context = LocalContext.current

        val data = remember {
            listOf(
                ChartData(Pink400, 10f),
                ChartData(Orange400, 20f),
                ChartData(Yellow400, 15f),
                ChartData(Green400, 5f),
                ChartData(Red400, 35f),
                ChartData(Blue400, 15f)
            )
        }

        PieChart(
            modifier = Modifier.fillMaxSize(),
            data = data,
            outerRingPercent = 35,
            innerRingPercent = 10,
            dividerStrokeWidth = 3.dp
        ) { chartData: ChartData, index: Int ->
            Toast.makeText(context, "Clicked: ${chartData.data}", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(10.dp))

        PieChart(
            modifier = Modifier.fillMaxSize(),
            data = data,
            outerRingPercent = 100,
            innerRingPercent = 0,
            startAngle = -90f,
            drawText = false,
            dividerStrokeWidth = 0.dp
        ) { chartData: ChartData, index: Int ->
            Toast.makeText(context, "Clicked: ${chartData.data}", Toast.LENGTH_SHORT).show()
        }

        Spacer(modifier = Modifier.height(10.dp))

        PieChart(
            modifier = Modifier.fillMaxSize(),
            data = data,
            outerRingPercent = 25,
            innerRingPercent = 0,
            dividerStrokeWidth = 2.dp
        ) { chartData: ChartData, index: Int ->
            Toast.makeText(context, "Clicked: ${chartData.data}", Toast.LENGTH_SHORT).show()
        }
    }
}
