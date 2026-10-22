package com.example.app02.ui.Screens.admin.MovieManage

import android.net.Uri
import android.widget.CalendarView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.R
import com.example.app02.models.Cate
import com.example.app02.models.Genre
import com.example.app02.models.Movie
import com.example.app02.models.DialogData
import com.example.app02.ui.Screens.user.LoadingIndicator
import com.example.app02.ui.components.BoldTextComponent
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.components.CustomizableSearchBar
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.MyTextField
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshad
import com.example.app02.ui.theme.boxshadow
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun MovieManageScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    movieViewModel: MovieViewModel = viewModel(),
    cateViewModel: CateViewModel = viewModel(),
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val uiState by movieViewModel.uiState.collectAsState()
    val cates = cateViewModel.cate.value
    var dialogData by remember { mutableStateOf<DialogData?>(null) }
    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabTitles = listOf("Danh sách phim", "Thêm phim")

    LaunchedEffect(Unit) {
        movieViewModel.fetchMovies()
        cateViewModel.fetchCate()
    }

    // Lắng nghe thông báo từ ViewModel
    LaunchedEffect(uiState.userMessage, uiState.errorMessage) {
        uiState.userMessage?.let { msg ->
            dialogData = DialogData(
                title = "Thao tác thành công",
                message = msg,
                dismissText = null,
                icon = Icons.Default.CheckCircle,
                onConfirm = { dialogData = null },
                onDismiss = { dialogData = null }
            )
            movieViewModel.userMessageShown()
            if (msg.contains("Thêm phim", ignoreCase = true)) {
                selectedTabIndex = 0 // Tự động quay về tab danh sách khi thêm thành công
            }
        }
        uiState.errorMessage?.let { error ->
            dialogData = DialogData(
                title = "Không thể hoàn tất thao tác",
                message = error,
                dismissText = null,
                icon = Icons.Default.ErrorOutline,
                onConfirm = { dialogData = null },
                onDismiss = { dialogData = null }
            )
            movieViewModel.userMessageShown()
        }
    }

    dialogData?.let { CommonAlertDialog(it) }

    CommonScaffold(isLogged, navController, loginViewModel, "admin") { p ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(p)
        ) {
            CustomTabRow(
                tabTitles = tabTitles,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = { index -> selectedTabIndex = index }
            )

            when (selectedTabIndex) {
                0 -> {
                    if (uiState.isLoading && uiState.movies.isEmpty()) {
                        LoadingIndicator()
                    } else {
                        MovieList(
                            cates = cates,
                            movies = uiState.movies,
                            navController = navController,
                            movieViewModel = movieViewModel
                        )
                    }
                }

                1 -> NewMovieScreen(
                    cates = cates,
                    movieViewModel = movieViewModel
                )
            }
        }
    }
}

@Composable
fun MovieList(
    cates: List<Cate>,
    movies: List<Movie>,
    navController: NavController,
    movieViewModel: MovieViewModel,
) {
    val searchQuery by movieViewModel.searchQuery.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Hàng chọn thể loại (thay weight bằng width để tránh crash khi cuộn ngang)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            cates.forEach { cate ->
                Card(
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .clickable { movieViewModel.fetchMoviesByCate(cate.id) }
                        .customShadow(
                            color = boxshad,
                            alpha = 0.2f,
                            borderRadius = 16.dp,
                            shadowRadius = 10.dp,
                            offsetX = 4.dp,
                            offsetY = 4.dp
                        ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cate.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        CustomizableSearchBar(
            query = searchQuery,
            onQueryChange = { movieViewModel.onSearchQueryChanged(it) },
            onSearch = { /* Đã được ViewModel tự debounce */ },
            searchResults = movies,
            onResultClick = { id ->
                navController.navigate("movie/$id")
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(movies) { movie ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bcl)
                        .customShadow(
                            color = boxshadow,
                            alpha = 0.2f,
                            borderRadius = 16.dp,
                            shadowRadius = 10.dp,
                            offsetX = 2.dp,
                            offsetY = 2.dp
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("movie/${movie.id}")
                        }
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = boxshadow)
                    ) {
                        Row {
                            Box(modifier = Modifier.weight(1f)) {
                                val posterModel = if (movie.poster.startsWith("content://")) {
                                    Uri.parse(movie.poster)
                                } else {
                                    movie.poster
                                }

                                Image(
                                    painter = rememberAsyncImagePainter(model = posterModel),
                                    contentDescription = "Movie Poster",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                )
                            }
                            Box(modifier = Modifier.weight(3f)) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalAlignment = Alignment.Start,
                                ) {
                                    Text(
                                        text = movie.title,
                                        style = TextStyle(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.LightGray
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LightTextComponent(movie.title)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NewMovieScreen(
    cates: List<Cate>,
    movieViewModel: MovieViewModel
) {
    val uiState by movieViewModel.uiState.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var posterUri by remember { mutableStateOf<Uri?>(null) }
    var cast by remember { mutableStateOf("") }
    var director by remember { mutableStateOf("") }
    var trailer by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var start by remember { mutableStateOf("") }
    var end by remember { mutableStateOf("") }

    var selectedCate by remember(cates) {
        mutableStateOf(cates.firstOrNull() ?: Cate(1, "Đang chiếu"))
    }
    val genres = listOf(
        Genre(1, "Hành động"),
        Genre(2, "Kinh dị"),
        Genre(3, "Hài hước"),
        Genre(4, "Tình cảm"),
        Genre(5, "Gia đình")
    )
    var selectedGenre by remember { mutableStateOf(genres.first()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        posterUri = uri
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            MyTextField(
                labelValue = "Tên phim",
                leadingIconVector = Icons.Default.VideoLibrary,
                value = title,
                onValueChange = { title = it }
            )
        }

        item {
            MyTextField(
                labelValue = "Mô tả",
                leadingIconVector = Icons.Default.Description,
                value = description,
                onValueChange = { description = it }
            )
        }

        item {
            OutlinedTextField(
                value = duration,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        duration = input
                    }
                },
                label = { Text("Thời lượng (phút)") },
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedLeadingIconColor = Color.White,
                    unfocusedLeadingIconColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    focusedContainerColor = bcl,
                    unfocusedContainerColor = bcl,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White,
                    focusedPlaceholderColor = Color.White,
                    unfocusedPlaceholderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        item {
            Text(
                "Poster phim",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.2f))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (posterUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(posterUri),
                        contentDescription = "poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Chạm để chọn ảnh", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            MyTextField(
                labelValue = "Diễn viên",
                leadingIconVector = Icons.Default.Person,
                value = cast,
                onValueChange = { cast = it }
            )
        }

        item {
            MyTextField(
                labelValue = "Đạo diễn",
                leadingIconVector = Icons.Default.PersonOutline,
                value = director,
                onValueChange = { director = it }
            )
        }

        item {
            MyTextField(
                labelValue = "Trailer",
                leadingIconVector = Icons.Default.PersonOutline,
                value = trailer,
                onValueChange = { trailer = it }
            )
        }

        item {
            Text(
                text = "Ngày bắt đầu",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp)
            ) {
                AndroidView(
                    factory = { CalendarView(it) },
                    modifier = Modifier.fillMaxSize(),
                    update = { calendarView ->
                        startDate?.let {
                            val millis =
                                it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            calendarView.date = millis
                        }
                        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                            startDate = LocalDate.of(year, month + 1, dayOfMonth)
                            start = startDate.toString()
                        }
                    }
                )
            }
        }

        item {
            Text(
                text = "Ngày kết thúc",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp)
            ) {
                AndroidView(
                    factory = { CalendarView(it) },
                    modifier = Modifier.fillMaxSize(),
                    update = { calendarView ->
                        endDate?.let {
                            val millis =
                                it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            calendarView.date = millis
                        }
                        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                            endDate = LocalDate.of(year, month + 1, dayOfMonth)
                            end = endDate.toString()
                        }
                    }
                )
            }
        }

        item {
            LabeledDropdown(
                label = "Danh mục",
                items = cates,
                selectedItem = selectedCate,
                onItemSelected = { selectedCate = it },
                itemToText = { it.name },
                icon = Icons.Default.Category
            )
        }

        item {
            LabeledDropdown(
                label = "Thể loại",
                items = genres,
                selectedItem = selectedGenre,
                onItemSelected = { selectedGenre = it },
                itemToText = { it.name },
                icon = Icons.Default.Movie
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Btn("Thêm", onClick = {
                    val movie = Movie(
                        title = title,
                        des = description,
                        genre = selectedGenre,
                        duration = duration.toIntOrNull() ?: 0,
                        poster = posterUri?.toString() ?: "",
                        cast = cast,
                        avg = 0.0,
                        director = director,
                        startDate = start,
                        endDate = end,
                        category = selectedCate,
                        trailer = trailer
                    )
                    movieViewModel.createMovie(movie)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun <T> LabeledDropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit,
    itemToText: (T) -> String,
    icon: ImageVector = Icons.Default.ArrowDropDown
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemToText(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToText(item)) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewMovieList() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .customShadow(
                    color = boxshadow,
                    alpha = 0.2f,
                    borderRadius = 16.dp,
                    shadowRadius = 10.dp,
                    offsetX = 4.dp,
                    offsetY = 4.dp
                )
                .clip(RoundedCornerShape(5.dp))
                .padding(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxSize(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = boxshadow)
            ) {
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        Image(
                            painter = painterResource(id = R.drawable.batman),
                            contentDescription = "Movie Poster",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(modifier = Modifier.weight(3f)) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            BoldTextComponent("title")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(8.dp))
    }
}