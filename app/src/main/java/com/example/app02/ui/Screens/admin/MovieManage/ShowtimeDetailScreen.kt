package com.example.app02.ui.Screens.admin.MovieManage

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.models.Showtime
import com.example.app02.models.DialogData
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import com.example.app02.ui.components.BackOnlyTopBar
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.ui.components.getHourAndMinute
import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.BookingViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
@Composable
fun ShowtimeDetailScreen(
    movieId: Int,
    navController: NavController,
    loginViewModel: LoginViewModel,
    showtimeViewModel: ShowtimeViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {
    val movieUiState by movieViewModel.uiState.collectAsState()
    val movie = movieUiState.selectedMovie

    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)

    val today = LocalDate.now()
    var selectedDate by rememberSaveable { mutableStateOf(today) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val showtimes by showtimeViewModel.showtimes.collectAsState()
    val isLoading by showtimeViewModel.isLoading.collectAsState()
    val error by showtimeViewModel.error.collectAsState()
    val hasLoadedShowtimes by showtimeViewModel.hasLoadedShowtimes.collectAsState()

    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    // Tải thông tin phim và suất chiếu theo ngày đã chọn.
    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId)
    }

    LaunchedEffect(movieId, selectedDate) {
        showtimeViewModel.fetchShowtimes(
            movieId = movieId,
            date = selectedDate.format(dateFormatter)
        )
    }

    LaunchedEffect(
        isLoading,
        hasLoadedShowtimes,
        showtimes,
        error
    ) {
        when {
            // Đang gọi API: không hiển thị dialog
            isLoading -> {
                dialogData = null
            }

            // API lỗi: chỉ hiển thị dialog lỗi
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
                    }
                )
            }

            // API đã trả kết quả thành công và danh sách rỗng
            hasLoadedShowtimes && showtimes.isEmpty() -> {
                dialogData = DialogData(
                    title = "Chưa có suất chiếu",
                    message = "Ngày bạn chọn hiện chưa có suất chiếu nào. Hãy chọn ngày khác hoặc tạo suất chiếu mới.",
                    dismissText = null,
                    icon = Icons.Default.Info,
                    onConfirm = {
                        dialogData = null
                    },
                    onDismiss = {
                        dialogData = null
                    }
                )
            }

            // Có dữ liệu: không hiển thị dialog
            hasLoadedShowtimes && showtimes.isNotEmpty() -> {
                dialogData = null
            }
        }
    }

    CommonScaffold(
        isLogged,
        navController,
        loginViewModel,
        "admin",
        showTopBar = false
    ) { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF111111))
                .padding(p)
        ) {
            movie?.let {
                BackOnlyTopBar(
                    navController,
                    title = it.title
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Chọn ngày:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textFocus
                    )

                    IconButton(
                        onClick = {
                            navController.navigate("create_showtime/$movieId")
                        },
                        modifier = Modifier.background(
                            Color(0xFF2A2A2A),
                            RoundedCornerShape(12.dp)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "add",
                            tint = Color.White
                        )
                    }
                }

                LazyRow {
                    items(4) { i ->
                        val date = today.plusDays(i.toLong())

                        Button(
                            onClick = {
                                selectedDate = date
                            },
                            modifier = Modifier.padding(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedDate == date) {
                                    Color(0xFF2F2C2C)
                                } else {
                                    Color.Gray
                                }
                            )
                        ) {
                            Text(
                                date.format(dateFormatter),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    error != null -> {
                        Text(
                            text = error.orEmpty(),
                            color = Color.Red
                        )

                        Button(
                            onClick = {
                                showtimeViewModel.fetchShowtimes(
                                    movieId,
                                    selectedDate.format(dateFormatter)
                                )
                            }
                        ) {
                            Text("Thử lại")
                        }
                    }

                    showtimes.isNotEmpty() -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(
                                items = showtimes,
                                key = { it.id }
                            ) { showtime ->
                                ShowtimeItemAdmin(showtime)
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

@Composable
fun ShowtimeItemAdmin(showtime: Showtime) {
    val countViewModel: BookingViewModel = viewModel()
    val startTime = getHourAndMinute(showtime.startTime).joinToString(":")
    val endTime = getHourAndMinute(showtime.endTime).joinToString(":")
    var showDialog by remember { mutableStateOf(false) }
    val counted by countViewModel.counted.collectAsState()

    LaunchedEffect(Unit) {
        countViewModel.countSeat(showtime.id)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(startTime, color = Color.White, fontWeight = FontWeight.Bold)
                Divider(
                    modifier = Modifier.height(18.dp).width(1.dp),
                    color = Color(0xFF4A4A4A)
                )
                Text(endTime, color = Color(0xFFBDBDBD))
            }

            Button(
                onClick = {
                    showDialog = true
                    countViewModel.countSeat(showtime.id)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F), contentColor = Color(0xFF111111))
            ) {
                Text("Trạng thái")
            }
        }
    }
    if (showDialog) {
        CommonAlertDialog(
            title = "Trạng thái ghế đã đặt",
            message = "Số ghế đã được đặt cho suất chiếu này: $counted.",
            confirmText = "Đóng",
            dismissText = null,
            icon = Icons.Default.EventSeat,
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false }
        )
    }

}

