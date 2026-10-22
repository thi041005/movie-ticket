package com.example.app02.ui.Screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app02.models.Movie
import com.example.app02.models.Showtime
import com.example.app02.models.DialogData
import com.example.app02.ui.components.*
import com.example.app02.ui.player.YouTubePlayer
import com.example.app02.ui.theme.textFocus
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.RatingViewModel
import com.example.app02.viewmodel.ShowtimeViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class) // Suppress experimental API warning
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavController,
    loginViewModel: LoginViewModel,
    role: String,
    movieViewModel: MovieViewModel = viewModel(),
    showtimeViewModel: ShowtimeViewModel = viewModel()
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val uiState by movieViewModel.uiState.collectAsState()
    val movie = uiState.selectedMovie
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    var selecledShowtime by rememberSaveable { mutableStateOf<Showtime?>(null) }

    val tabTitles = listOf("Chi tiết", "Suất chiếu")
    LaunchedEffect(movieId) {
        movieViewModel.fetchMovieDetails(movieId)
    }

    CommonScaffold(isLogged, navController, loginViewModel, role, showTopBar = false) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            BackOnlyTopBar(navController, title = "Chi tiết phim")
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )

            when (selectedTabIndex) {
                0 -> {
                    if (movie == null) {
                        LoadingIndicator()
                    } else {
                        MovieDetailContent(movie)
//                        Btn(
//                            value = "Chọn suất",
//                            onClick = { selectedTabIndex = 1 }
//                        )
                    }
                }

                1 -> MovieShowtimes(movieId, navController = navController)
            }

        }
    }
}

@Composable
fun MovieShowtimes(
    movieId: Int,
    showtimeViewModel: ShowtimeViewModel = viewModel(),
    navController: NavController
) {

    val today = LocalDate.now()
    var selectedDate by rememberSaveable { mutableStateOf(today) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val showtime by showtimeViewModel.showtimes.collectAsState(initial = emptyList())
    val isLoading by showtimeViewModel.isLoading.collectAsState()
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    // Dùng snapshotFlow để theo dõi selectedDate thay vì LaunchedEffect
    LaunchedEffect(movieId) {
        snapshotFlow { selectedDate }
            .collectLatest { date ->
                showtimeViewModel.fetchShowtimes(movieId.toInt(), date.format(dateFormatter))
            }
    }
//    LaunchedEffect(Unit) {
//        showtimeViewModel.emptyShowtimeEvent.collect {
//            dialogData = DialogData(
//                title = "Chưa có suất chiếu",
//                message = "Ngày bạn chọn hiện chưa có suất chiếu. Vui lòng chọn ngày khác để tiếp tục đặt vé.",
//                dismissText = null,
//                icon = Icons.Default.Info,
//                onConfirm = { dialogData = null },
//                onDismiss = { dialogData = null }
//            )
//        }
//    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Chọn ngày:", style = MaterialTheme.typography.bodyMedium, color = textFocus)

        LazyRow {
            items(4) { i ->
                val date = today.plusDays(i.toLong())
                Button(
                    onClick = { selectedDate = date },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedDate == date) Color(0xFF2F2C2C) else Color.Gray
                    )
                ) {
                    Text(date.format(dateFormatter), color = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (showtime.isNotEmpty()) {
            LazyColumn {
                items(showtime) { s ->
                    ShowtimeItem(s, navController)
                }
            }
        }
    }
    dialogData?.let { CommonAlertDialog(it) }
}


@Composable
fun MovieDetailContent(movie: Movie?, ratingViewModel: RatingViewModel = viewModel()) {
    val rates by ratingViewModel.result.collectAsState()
    var text by remember { mutableStateOf("") }
    var selectedScore by remember { mutableStateOf(8) }
    var expanded by remember { mutableStateOf(false) }
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    val sortedRates = remember(rates) { rates.sortedByDescending { it.ratedAt } }
    val averageRating = remember(sortedRates) {
        if (sortedRates.isEmpty()) 0.0 else sortedRates.map { it.score.toDouble() }.average()
    }

    LaunchedEffect(rates) {
        movie?.id?.let {

            ratingViewModel.getRating(it)
        }
    }
    LaunchedEffect(movie == null) {
        if (movie == null) {
            dialogData = DialogData(
                title = "Không tìm thấy phim",
                message = "Thông tin bộ phim không tồn tại hoặc không thể tải vào lúc này.",
                dismissText = null,
                icon = Icons.Default.ErrorOutline,
                onConfirm = { dialogData = null },
                onDismiss = { dialogData = null }
            )
        }
    }

    movie?.let {
        val videoId = "dQw4w9WgXcQ"

        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            YouTubePlayer(
                youtubeVideoId = "NLOp_6uPccQ",
                lifecycleOwner = LocalLifecycleOwner.current
            )
            Text(
                text = movie.des,
                modifier = Modifier
                    .fillMaxWidth(),
                style = TextStyle(
                    fontSize = 16.sp, fontStyle = FontStyle.Normal
                ),
                color = Color.White,
                textAlign = TextAlign.Justify // Căn đều chữ theo chiều ngang
            )
            Spacer(modifier = Modifier.height(8.dp))
            RowComponent("Thể loại", "${movie.genre.name}")
            RowComponent("Thời lượng", "${movie.duration} phút")
            RowComponent("Đạo diễn", "${movie.director}")
            RowComponent("Diễn viên chính", "${movie.cast}")

            Spacer(modifier = Modifier.padding(15.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Viết đánh giá",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        placeholder = {
                            Text(
                                text = "Chia sẻ cảm nhận của bạn về bộ phim...",
                                color = Color(0xFF9C9C9C)
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFFD54F),
                            unfocusedBorderColor = Color(0xFF3A3A3A),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFFFFD54F),
                            focusedContainerColor = Color(0xFF121212),
                            unfocusedContainerColor = Color(0xFF121212)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Chọn điểm sao",
                            color = Color(0xFFEAEAEA),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "$selectedScore/10",
                            color = Color(0xFFFFD54F),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..10).forEach { score ->
                            IconButton(
                                onClick = { selectedScore = score },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (score <= selectedScore) Color.Yellow else Color(0xFF5A5A5A),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            movie?.id?.let {
                                ratingViewModel.rating(it, selectedScore, text) { success ->
                                    if (success) {
                                        ratingViewModel.getRating(it)
                                        dialogData = DialogData(
                                            title = "Đánh giá đã được gửi",
                                            message = "Cảm ơn bạn đã chia sẻ cảm nhận về bộ phim.",
                                            dismissText = null,
                                            icon = Icons.Default.CheckCircle,
                                            onConfirm = { dialogData = null },
                                            onDismiss = { dialogData = null }
                                        )
                                        text = ""
                                    } else {
                                        dialogData = DialogData(
                                            title = "Không thể gửi đánh giá",
                                            message = "Đánh giá chưa được lưu. Vui lòng kiểm tra kết nối và thử lại.",
                                            dismissText = null,
                                            icon = Icons.Default.ErrorOutline,
                                            onConfirm = { dialogData = null },
                                            onDismiss = { dialogData = null }
                                        )
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD54F),
                            contentColor = Color(0xFF111111)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Đăng đánh giá",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tổng điểm trung bình của phim",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (sortedRates.isEmpty()) "0.0/10" else "${"%.1f".format(averageRating)}/10",
                        color = Color(0xFFFFD54F),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sortedRates.forEach { rate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B1B1B)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = rate.username,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        val filledStars = ((rate.score / 2.0).roundToInt()).coerceIn(0, 5)
                                        repeat(5) { index ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (index < filledStars) Color.Yellow else Color(0xFF5A5A5A),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${rate.score}/10",
                                            color = Color(0xFFFFD54F),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = rate.ratedAt.toString(),
                                    color = Color(0xFFB0B0B0),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.End
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF121212), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = rate.comment.ifBlank { "Không có nhận xét chi tiết." },
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(8.dp))

            dialogData?.let { CommonAlertDialog(it) }
        }
    } ?: Unit
}

@Composable
fun ShowtimeItem(showtime: Showtime, navController: NavController) {
    val startTime = getHourAndMinute(showtime.startTime).joinToString(":")
    val endTime = getHourAndMinute(showtime.endTime).joinToString(":")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        NormalTextComponent(startTime)
        Spacer(modifier = Modifier.width(8.dp))
        Divider(
            modifier = Modifier
                .height(24.dp)
                .width(2.dp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        NormalTextComponent(endTime)
        Btn("Chọn ghế", onClick = { navController.navigate("showSeat/${showtime.id}") })

    }
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        color = Color.Gray,
        thickness = 1.dp
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreviewOfDetailScreen() {
//    val navController = rememberNavController() // Tạo NavController giả để tránh lỗi
//    MovieDetailScreen(movieId = 1, navController = navController)
//}
//
