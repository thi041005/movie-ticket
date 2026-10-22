package com.example.app02.ui.Screens.admin.MovieManage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.LightTextComponentCenter
import com.example.app02.ui.components.RowComponent
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import com.example.app02.models.DialogData
import com.example.app02.ui.components.DateRangePickerModal
import com.example.app02.ui.components.DialExample
import com.example.app02.ui.components.ScheduleTimeSettings
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun CreateScheduleScreen(
    movieId: Int,
    loginViewModel: LoginViewModel, navController: NavController,
    scheduleViewModel: ShowtimeViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {

    val movieUiState by movieViewModel.uiState.collectAsState()
    val movie = movieUiState.selectedMovie

    var fromDate by remember { mutableStateOf(LocalDate.now()) }
    var toDate by remember { mutableStateOf(LocalDate.now().plusDays(2)) }
    var dailyStart by remember { mutableStateOf(LocalTime.of(9, 0)) }
    var dailyEnd by remember { mutableStateOf(LocalTime.of(22, 0)) }
    var showDatePicker by remember { mutableStateOf(false) }
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId)
    }
    CommonScaffold(isLogged, navController, loginViewModel, "admin", showTopBar = false) { p ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(p)
                .background(Color(0xFF111111))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            movie?.let {
                com.example.app02.ui.components.BackOnlyTopBar(
                    navController,
                    title = it.title
                )

            }

            ScheduleTimeSettings(
                fromDate = fromDate,
                toDate = toDate,
                dailyStart = dailyStart,
                dailyEnd = dailyEnd,
                onDateRangeClick = {
                    showDatePicker = true
                },
                onStartTimeClick = {
                    showStartTimePicker = true
                },
                onEndTimeClick = {
                    showEndTimePicker = true
                }
            )

            Btn("Lập lịch", onClick = {
                movie?.id?.let {
                    scheduleViewModel.scheduleByMovie(
                        it,
                        fromDate,
                        toDate,
                        dailyStart,
                        dailyEnd
                    ) { success ->
                        if (success) {
                            dialogData = DialogData(
                                title = "Lập lịch thành công",
                                message = "Các suất chiếu đã được tạo. Vui lòng kiểm tra lại lịch trước khi công bố.",
                                dismissText = null,
                                icon = Icons.Default.CheckCircle,
                                onConfirm = { dialogData = null },
                                onDismiss = { dialogData = null }
                            )
                        } else {
                            dialogData = DialogData(
                                title = "Không thể lập lịch",
                                message = "Không thể tạo suất chiếu lúc này. Vui lòng kiểm tra khoảng thời gian và thử lại.",
                                dismissText = null,
                                icon = Icons.Default.ErrorOutline,
                                onConfirm = { dialogData = null },
                                onDismiss = { dialogData = null }
                            )
                        }
                    }
                }
            })

            if (showDatePicker) {
                DateRangePickerModal(
                    onDateRangeSelected = { pair ->
                        pair.first?.let { millisStart ->
                            fromDate = Instant.ofEpochMilli(millisStart)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        pair.second?.let { millisEnd ->
                            toDate = Instant.ofEpochMilli(millisEnd)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

            if (showStartTimePicker) {
                DialExample(
                    initialTime = dailyStart,
                    onConfirm = {
                        dailyStart = it
                        showStartTimePicker = false
                    },
                    onDismiss = { showStartTimePicker = false })
            }
            if (showEndTimePicker) {
                DialExample(
                    initialTime = dailyEnd,
                    onConfirm = {
                        dailyEnd = it
                        showEndTimePicker = false
                    },
                    onDismiss = { showEndTimePicker = false })
            }
        }
    }
    dialogData?.let { CommonAlertDialog(it) }
}