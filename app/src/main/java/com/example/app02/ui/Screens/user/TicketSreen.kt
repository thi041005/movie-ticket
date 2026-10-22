package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.R
import com.example.app02.ui.components.BackOnlyTopBar
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.models.DialogData
import com.example.app02.ui.components.LightTextComponentCenter
import com.example.app02.ui.components.RowComponent2
import com.example.app02.ui.theme.bcl
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.TicketUiState
import com.example.app02.viewmodel.TicketViewModel
import java.time.format.DateTimeFormatter
@Composable
fun TicketScreen(
    navController: NavController,
    bookingId: Int,
    ticketViewModel: TicketViewModel,
    loginViewModel: LoginViewModel,
) {
    val isLogged by loginViewModel.isLogged.collectAsState(
        initial = false
    )

    val ticketUiState by ticketViewModel.ticketUiState
        .collectAsState()

    var dialogData by remember(bookingId) {
        mutableStateOf<DialogData?>(null)
    }

    LaunchedEffect(bookingId) {
        ticketViewModel.getTicket(bookingId)
    }

    LaunchedEffect(ticketUiState, bookingId) {
        dialogData = when (val state = ticketUiState) {
            is TicketUiState.Loading,
            is TicketUiState.Success -> null

            is TicketUiState.NotFound -> DialogData(
                title = "Chưa lấy được thông tin vé",
                message = "Hệ thống chưa lấy được thông tin vé. " +
                        "Vui lòng thử tải lại.",
                confirmText = "Tải lại",
                dismissText = "Quay lại",
                icon = Icons.Default.ErrorOutline,
                onConfirm = {
                    dialogData = null
                    ticketViewModel.retryGetTicket(bookingId)
                },
                onDismiss = {
                    dialogData = null
                    navController.popBackStack()
                }
            )

            is TicketUiState.Error -> DialogData(
                title = "Không thể tải vé",
                message = state.message,
                confirmText = "Thử lại",
                dismissText = "Quay lại",
                icon = Icons.Default.ErrorOutline,
                onConfirm = {
                    dialogData = null
                    ticketViewModel.retryGetTicket(bookingId)
                },
                onDismiss = {
                    dialogData = null
                    navController.popBackStack()
                }
            )
        }
    }

    CommonScaffold(
        isLogged,
        navController,
        loginViewModel,
        "user",
        showTopBar = false
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BackOnlyTopBar(
                navController,
                title = "Vé của tôi"
            )

            when (val state = ticketUiState) {
                is TicketUiState.Loading -> {
                    Spacer(modifier = Modifier.height(32.dp))

                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Thanh toán thành công!",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Đang tạo và tải thông tin vé..."
                    )
                }

                is TicketUiState.NotFound,
                is TicketUiState.Error -> {
                    // Dialog được hiển thị bên ngoài CommonScaffold.
                }

                is TicketUiState.Success -> {
                    val ticket = state.ticket

                    val seatText = ticket.seat.joinToString(", ") {
                        "${it.row}${it.col}"
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .background(bcl),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            LightTextComponentCenter(
                                "Không chia sẻ cho bất kỳ ai!"
                            )
                        }

                        item {
                            LightTextComponentCenter(
                                "Cung cấp mã QR này cho nhân viên tại quầy để xác nhận!"
                            )
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                                    .background(bcl),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // QR vé
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 30.dp,
                                                topEnd = 30.dp
                                            )
                                        )
                                        .size(400.dp)
                                        .background(
                                            com.example.app02.ui.theme.ticket
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!ticket.qr.isNullOrBlank()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(
                                                model = ticket.qr
                                            ),
                                            contentDescription = "QR vé",
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.fillMaxSize(0.7f)
                                        )
                                    } else {
                                        Text("Vé chưa có mã QR")
                                    }
                                }

                                // Đường cắt vé
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            com.example.app02.ui.theme.ticket
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(0.1f)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.noun_half_circle_left
                                            ),
                                            contentDescription = "Nửa hình tròn bên trái",
                                            contentScale = ContentScale.Fit
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(0.8f)
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        repeat(10) {
                                            Divider(
                                                modifier = Modifier
                                                    .height(5.dp)
                                                    .width(20.dp),
                                                color = bcl
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier.fillMaxWidth(0.1f)
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                id = R.drawable.right
                                            ),
                                            contentDescription = "Nửa hình tròn bên phải",
                                            contentScale = ContentScale.Fit
                                        )
                                    }
                                }

                                // Thông tin vé
                                Column(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                bottomStart = 30.dp,
                                                bottomEnd = 30.dp
                                            )
                                        )
                                        .fillMaxWidth()
                                        .background(
                                            com.example.app02.ui.theme.ticket
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    RowComponent2(
                                        "Thời gian: ",
                                        ticket.payTime.format(
                                            DateTimeFormatter.ofPattern(
                                                "dd/MM/yyyy HH:mm"
                                            )
                                        )
                                    )

                                    Spacer(
                                        modifier = Modifier.height(5.dp)
                                    )

                                    RowComponent2(
                                        "Phim: ",
                                        ticket.movieName
                                    )

                                    RowComponent2(
                                        "Phòng chiếu: ",
                                        ticket.audName
                                    )

                                    RowComponent2(
                                        "Ghế: ",
                                        seatText
                                    )

                                    RowComponent2(
                                        "Đã thanh toán: ",
                                        "${ticket.total.toInt()} VND"
                                    )

                                    RowComponent2(
                                        "Mã vé: ",
                                        ticket.ticketId.toString()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    dialogData?.let {
        CommonAlertDialog(it)
    }
}
