package com.example.app02.ui.Screens.user

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.R
import com.example.app02.models.Seat
import com.example.app02.ui.components.BackOnlyTopBar
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.NormalTextComponent
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.components.getDate
import com.example.app02.ui.components.getHourAndMinute
import com.example.app02.ui.theme.GradientBtn
import com.example.app02.ui.theme.reserved
import com.example.app02.ui.theme.textFocus
import com.example.app02.ui.theme.unreserved
import com.example.app02.ui.theme.vip
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.SeatViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import com.example.app02.models.DialogData
import com.example.app02.ui.components.CommonAlertDialog
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowSeatScreen(
    showtimeId: Int,
    navController: NavController,
    role: String,

    bookingViewModel: BookingViewModel = viewModel(),
    loginViewModel: LoginViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val showtimeViewModel: ShowtimeViewModel = viewModel()
    LaunchedEffect(showtimeId) {
        showtimeViewModel.fetchShowtimeById(showtimeId)
    }

    val showtime by showtimeViewModel.showtime.collectAsState()

    val startTime = showtime?.let { getHourAndMinute(it.startTime).joinToString(":") }
    val date = showtime?.let {
        getDate(it.startTime).let { listOf(it[2], it[1], it[0]) }.joinToString("/")
    }
    CommonScaffold(isLogged, navController, loginViewModel, role, showTopBar = false) { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackOnlyTopBar(navController, title = "Chọn ghế")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .border(1.dp, reserved, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            3.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "date",
                            tint = Color.White
                        )
                        if (date != null) {
                            NormalTextComponent(date)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .border(1.dp, reserved, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(
                            3.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "time",
                            tint = Color.White
                        )
                        if (startTime != null) {
                            NormalTextComponent(startTime)
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(unreserved, shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .border(0.dp, Color.Transparent, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                }
                NormalTextComponent("Đã bán")
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(reserved, shape = RoundedCornerShape(8.dp))
                        .padding(4.dp)
                        .border(0.dp, Color.Transparent, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "reserved",
                        tint = Color.White
                    )
                }
                NormalTextComponent("Còn trống")
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .customShadow(
                            color = textFocus,
                            alpha = 0.2f,
                            borderRadius = 16.dp,
                            shadowRadius = 10.dp,
                            offsetX = 4.dp,
                            offsetY = 4.dp
                        )
                        .background(GradientBtn, shape = RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {

                }

                NormalTextComponent("Đã chọn")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(150.dp),
                painter = painterResource(id = R.drawable.screen),
                contentDescription = "screen",
            )
            Spacer(modifier = Modifier.height(5.dp))

            if (showtime != null) {
                SeatLayout(showtimeId = showtimeId, navController, bookingViewModel, role)
            }
        }
    }
}

@Composable
fun SeatLayout(
    showtimeId: Int, navController: NavController, bookingViewModel: BookingViewModel, role: String
) {
    val seatViewModel: SeatViewModel = viewModel()
    val seats by seatViewModel.seats.collectAsState()
    val seatState by seatViewModel.seatState.collectAsState()
    val selectedSeats = remember { mutableStateListOf<Seat>() }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val heldSeatTime = remember { mutableStateMapOf<Int, Long>() }
    DisposableEffect(Unit) {
        onDispose {
            if (selectedSeats.isNotEmpty()) {
                selectedSeats.forEach { seat ->
                    seatViewModel.cancelHold(seat.id, showtimeId) { success ->

                    }

                }
            }
        }
    }
    LaunchedEffect(showtimeId) {
        seatViewModel.fetchSeats(showtimeId)
        seatViewModel.initWebSocket(showtimeId)
    }
    selectedSeats.forEach { seat ->
        LaunchedEffect(seat.id) {
            delay(1 * 60 * 1000)
            val selectedAt = heldSeatTime[seat.id] ?: return@LaunchedEffect
            if (System.currentTimeMillis() - selectedAt >= 1 * 60 * 1000) {
                selectedSeats.remove(seat)
                heldSeatTime.remove(seat.id)
                seatViewModel.cancelHold(seat.id, showtimeId) {}
                dialogData = DialogData(
                    title = "Ghế đã được trả lại",
                    message = "Ghế ${seat.row}${seat.row} đã được tự động bỏ chọn vì thời gian giữ ghế đã hết.",
                    dismissText = null,
                    icon = Icons.Default.ErrorOutline,
                    onConfirm = { dialogData = null },
                    onDismiss = { dialogData = null }
                )
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(seats.groupBy { it.row }.toList()) { (row, seatsInRow) ->
            Row(
                modifier = Modifier
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                seatsInRow.forEach { seat ->
                    val isSelected = selectedSeats.contains(seat)
                    Box(
                        modifier = Modifier
                            .size(25.dp)
                            .border(
                                0.dp,
                                Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )

                            .then(
                                if (isSelected) Modifier.customShadow(
                                    color = textFocus,
                                    alpha = 0.2f,
                                    borderRadius = 16.dp,
                                    shadowRadius = 10.dp,
                                    offsetX = 4.dp,
                                    offsetY = 4.dp
                                ) else Modifier
                            )
                            .background(
                                brush = when {
                                    isSelected -> GradientBtn
                                    else -> null

                                } ?: SolidColor(
                                    when {
                                        seat.state == 1 || seat.state == 3 -> reserved
                                        seat.type.id.let { it == 1 } -> vip
                                        else -> unreserved
                                    }),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = seat.state != 3) {
                                if (seat.state != 3 && !selectedSeats.contains(
                                        seat
                                    )
                                ) {

                                    seatViewModel.holdSeat(seat.id, showtimeId) { success ->
                                        if (success) {
                                            selectedSeats.add(seat)
                                            heldSeatTime[seat.id] =
                                                System.currentTimeMillis()
                                        } else {
                                            dialogData = DialogData(
                                                title = "Ghế không khả dụng",
                                                message = "Ghế này đang được khách hàng khác giữ. Vui lòng chọn ghế khác.",
                                                dismissText = null,
                                                icon = Icons.Default.ErrorOutline,
                                                onConfirm = { dialogData = null },
                                                onDismiss = { dialogData = null }
                                            )
                                        }
                                    }
                                } else if (selectedSeats.contains(seat)) {
                                    seatViewModel.cancelHold(
                                        seat.id,
                                        showtimeId
                                    ) { success ->
                                        if (success) {
                                            selectedSeats.remove(seat)
                                        } else {
                                            dialogData = DialogData(
                                                title = "Không thể bỏ chọn ghế",
                                                message = "Ghế đã được giữ bởi khách hàng khác. Vui lòng tải lại danh sách ghế.",
                                                dismissText = null,
                                                icon = Icons.Default.ErrorOutline,
                                                onConfirm = { dialogData = null },
                                                onDismiss = { dialogData = null }
                                            )
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (seat.state == 3) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "reserved",
                                tint = Color.White
                            )
                        } else {
                            Text(
                                seat.col.toString(),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.Center),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.padding(16.dp)) }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ghế đã chọn",
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2A2A2A), RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${selectedSeats.size}",
                                color = Color(0xFFFFD54F),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (selectedSeats.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(selectedSeats) { seat ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2D2D2D), RoundedCornerShape(12.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EventSeat,
                                            contentDescription = null,
                                            tint = Color(0xFFFFD54F),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "${seat.row}${seat.col}",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(
                        color = Color(0xFF3A3A3A),
                        thickness = 1.dp
                    )
                    val totalPrice = selectedSeats.sumOf { it.type.price }
                    val formatted = NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tổng tiền",
                            color = Color(0xFFBDBDBD),
                            style = MaterialTheme.typography.bodyLarge
                        )

                         Text(
                            text = if (selectedSeats.isEmpty()) "0 VND" else "$formatted VND",
                            color = Color(0xFFFFD54F),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Btn(
                        "Đặt vé",
                        onClick = {
                            if (selectedSeats.isEmpty()) {
                                dialogData = DialogData(
                                    title = "Chưa chọn ghế",
                                    message = "Vui lòng chọn ít nhất một ghế trước khi tiếp tục đặt vé.",
                                    dismissText = null,
                                    icon = Icons.Default.ErrorOutline,
                                    onConfirm = { dialogData = null },
                                    onDismiss = { dialogData = null }
                                )
                            } else {
                                bookingViewModel.selectedSeats = selectedSeats.toList()
                                bookingViewModel.bookSeat(
                                    showtimeId,
                                    selectedSeats.map { it.id.toString() },
                                    totalPrice
                                ) { response ->
                                    response?.let {
                                        val encodedUrl = Uri.encode(response.qrUrl)
                                        navController.navigate("payment/${response.bookingId}/${encodedUrl}/${showtimeId}")
                                    } ?: run {
                                        dialogData = DialogData(
                                            title = "Không thể đặt vé",
                                            message = "Đã xảy ra lỗi khi giữ ghế. Vui lòng chọn lại ghế và thử lại.",
                                            dismissText = null,
                                            icon = Icons.Default.ErrorOutline,
                                            onConfirm = { dialogData = null },
                                            onDismiss = { dialogData = null }
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    dialogData?.let { CommonAlertDialog(it) }

}
