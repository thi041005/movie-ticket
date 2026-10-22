package com.example.app02.ui.Screens.admin

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

import androidx.navigation.NavController
import com.example.app02.network.api.RevenueDTO
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.models.DialogData
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.RevenueViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

import java.text.NumberFormat
import java.util.*
import androidx.core.graphics.toColorInt

@Composable
fun HomeScreen(
    navController: NavController, loginViewModel: LoginViewModel, revenueViewModel: RevenueViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val revenues by revenueViewModel.revenues.collectAsState()
    val isLoading by revenueViewModel.isLoading.collectAsState()
    val error by revenueViewModel.error.collectAsState()
    val hasLoadedRevenue by revenueViewModel.hasLoadedRevenue.collectAsState()

    var dialogData by remember { mutableStateOf<DialogData?>(null) }


    LaunchedEffect(Unit) {
        revenueViewModel.fetchRevenues()
    }

    LaunchedEffect(isLoading, error) {
        when {
            isLoading -> {
                dialogData = null
            }

            error != null -> {
                dialogData = DialogData(
                    title = "Lỗi tải dữ liệu",
                    message = error!!,
                    dismissText = null,
                    icon = Icons.Default.ErrorOutline,
                    onConfirm = {
                        dialogData = null
                    },
                    onDismiss = {
                        dialogData = null
                    })
            }

            else -> {
                dialogData = null
            }

        }
    }

    CommonScaffold(
        isLogged, navController, loginViewModel, "admin"
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF111111))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tổng quan doanh thu",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            when {
                // 1. Đang tải API
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // 2. API lỗi: không hiển thị biểu đồ hoặc số liệu cũ
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không thể tải dữ liệu doanh thu.", color = Color.Gray
                        )
                    }
                }

                // 3. API đã trả kết quả thành công
                hasLoadedRevenue -> {
                    RevenueDaily(revenues)
                    RevenuePieChart(revenues)

                    // Chỉ hiển thị chi tiết khi có doanh thu
                    if (revenues.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1B1B1B)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Chi tiết theo ngày",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                revenues.forEach { revenue ->
                                    RevenueItem(
                                        revenue.date, revenue.total
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    dialogData?.let { dialog ->
        CommonAlertDialog(dialog)
    }
}

@Composable
fun RevenueItem(date: String, total: Double) {
    val formatter = remember { NumberFormat.getInstance(Locale("vi", "VN")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = date, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "${formatter.format(total)} đ",
            color = Color(0xFFFFD54F),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RevenueDaily(revenues: List<RevenueDTO>) {
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    LaunchedEffect(revenues.isEmpty()) {
        if (revenues.isEmpty()) {
            dialogData = DialogData(
                title = "Chưa có dữ liệu doanh thu",
                message = "Chưa phát sinh doanh thu trong khoảng thời gian được chọn.",
                dismissText = null,
                icon = Icons.Default.Info,
                onConfirm = { dialogData = null },
                onDismiss = { dialogData = null })
        }
    }
    if (revenues.isEmpty()) {
        dialogData?.let { CommonAlertDialog(it) }
        return
    }

    AndroidView(
        factory = { context ->
        LineChart(context).apply {
            description.isEnabled = false
            setBackgroundColor(android.graphics.Color.TRANSPARENT)

            // Trục bên phải không cần thiết
            axisRight.isEnabled = false

            // Trục X: ngày tháng
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = android.graphics.Color.LTGRAY
                textSize = 10f
                axisLineColor = android.graphics.Color.GRAY
            }

            // Trục Y: doanh thu
            axisLeft.apply {
                textColor = android.graphics.Color.LTGRAY
                textSize = 10f
                gridColor = "#383838".toColorInt()
                axisLineColor = android.graphics.Color.GRAY
            }

            legend.isEnabled = false
        }
    }, update = { chart ->
        val entries = revenues.mapIndexed { index, revenue ->
            Entry(index.toFloat(), revenue.total.toFloat())
        }

        val dataSet = LineDataSet(entries, "Doanh thu").apply {
            color = android.graphics.Color.DKGRAY

            valueTextColor = android.graphics.Color.WHITE
            lineWidth = 2f
            setDrawCircles(true)
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = "#2C2C2C".toColorInt()
        }

        chart.data = LineData(dataSet)

        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt().coerceIn(revenues.indices)
                return revenues.getOrNull(index)?.date ?: ""
            }
        }

        chart.invalidate()
    }, modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // ✅ GIỚI HẠN CHIỀU CAO Ở ĐÂY
    )

}

@Composable
fun RevenuePieChart(revenues: List<RevenueDTO>) {
    AndroidView(
        factory = { context ->
        PieChart(context).apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setEntryLabelTextSize(12f)
            setUsePercentValues(true)
            legend.isEnabled = true
        }
    }, update = { chart ->
        if (revenues.isEmpty()) {
            chart.clear()
            return@AndroidView
        }

        val entries = revenues.map {
            PieEntry(it.total.toFloat(), it.date)
        }

        val dataSet = PieDataSet(entries, "Doanh thu theo ngày").apply {
            colors = listOf(
                "#121212".toColorInt(),
                "#2A2A2A".toColorInt(),
                "#4A4A4A".toColorInt(),
                "#666666".toColorInt(),
                "#8A8A8A".toColorInt()
            )
            valueTextColor = android.graphics.Color.WHITE
            valueTextSize = 14f
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(chart))
        }

        chart.data = data
        chart.invalidate()
    }, modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Composable
@Preview
fun RevenueChartPreview() {
    val fakeRevenues = listOf(
        RevenueDTO("05-01", 500.0),
        RevenueDTO("05-02", 1000.0),
        RevenueDTO("05-03", 1500.0),
        RevenueDTO("05-04", 800.0),
        RevenueDTO("05-05", 1300.0)
    )

    RevenueDaily(fakeRevenues)
}
