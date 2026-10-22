package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.ui.components.BackOnlyTopBar
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.models.DialogData
import com.example.app02.nav.Screen
import com.example.app02.ui.theme.reserved
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.SeatViewModel
import kotlinx.coroutines.delay

@Composable
fun PaymentScreen(
    navController: NavController,
    bookingId: Int,
    qrUrl: String,
    showtimeId: Int,
    bookingViewModel: BookingViewModel,
    loginViewModel: LoginViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val seatViewModel: SeatViewModel = viewModel()
    val seats = bookingViewModel.selectedSeats
    val countdown = remember { mutableStateOf(10 * 60) }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    var paymentConfirmed by remember(bookingId) {
        mutableStateOf(false)
    }

    var cancellationRequested by remember(bookingId) {
        mutableStateOf(false)
    }

    val shouldCancelOnDispose by rememberUpdatedState(
        !paymentConfirmed && !cancellationRequested
    )
    DisposableEffect(bookingId) {
        onDispose {
            if (shouldCancelOnDispose) {
                bookingViewModel.cancelBook(bookingId) {}
            }
        }
    }

    LaunchedEffect(bookingId) {
        if (seats.isEmpty()) {
            dialogData = DialogData(
                title = "Chưa có ghế trong đơn đặt vé",
                message = "Đơn đặt vé không có thông tin ghế. Vui lòng quay lại và chọn ghế lại.",
                confirmText = "Chọn lại ghế",
                dismissText = null,
                icon = Icons.Default.ErrorOutline,
                onConfirm = {
                    dialogData = null
                    navController.popBackStack()
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
            return@LaunchedEffect
        }

        var elapsedSeconds = 0

        while (countdown.value > 0) {
            // Kiểm tra ngay lần đầu, sau đó khoảng mỗi 2 giây.
            if (elapsedSeconds % 2 == 0) {
                val status =
                    bookingViewModel.refreshBookingState(bookingId)

                if (status == 1) {
                    paymentConfirmed = true

                    navController.navigate("ticket/$bookingId") {
                        popUpTo(Screen.ShowSeat.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                    return@LaunchedEffect
                }
            }

            delay(1000)

            countdown.value =
                (countdown.value - 1).coerceAtLeast(0)

            elapsedSeconds++
        }

        // Kiểm tra lần cuối khi hết giờ, tránh hủy booking
        // nếu webhook vừa cập nhật trạng thái thanh toán.
        val finalStatus =
            bookingViewModel.refreshBookingState(bookingId)

        if (finalStatus == 1) {
            paymentConfirmed = true

            navController.navigate("ticket/$bookingId") {
                popUpTo(Screen.ShowSeat.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        cancellationRequested = true

        bookingViewModel.cancelBook(bookingId) { success ->
            if (success) {
                seatViewModel.fetchSeats(showtimeId)
            }
        }

        dialogData = DialogData(
            title = "Đã hết thời gian thanh toán",
            message = "Phiên giữ ghế đã kết thúc. Vui lòng chọn ghế lại để tiếp tục.",
            confirmText = "Chọn lại ghế",
            dismissText = null,
            icon = Icons.Default.ErrorOutline,
            onConfirm = {
                dialogData = null
                navController.popBackStack()
            },
            onDismiss = {
                dialogData = null
            }
        )
    }

    CommonScaffold(isLogged, navController, loginViewModel, "user", showTopBar = false) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BackOnlyTopBar(navController, title = "Thanh toán")

            val min = countdown.value / 60
            val sec = countdown.value % 60

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1B))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "time",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Thời gian còn lại",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            color = Color(0xFFFFD54F),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121212))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mã QR thanh toán",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = rememberAsyncImagePainter(model = qrUrl),
                        contentDescription = "QR thanh toan",
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .padding(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Mã đặt vé",
                        color = Color(0xFFBDBDBD),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2A2A2A), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "#$bookingId",
                            color = Color(0xFFFFD54F),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Ghế đã chọn",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    if (seats.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            seats.forEach { seat ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF2A2A2A), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "${seat.row}${seat.col}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFFFD54F),
                        strokeWidth = 2.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Đang chờ xác nhận thanh toán...",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    dialogData?.let { CommonAlertDialog(it) }
}

