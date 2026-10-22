package com.example.app02.ui.Screens.user

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState


import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.models.Movie
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomizableSearchBar
import com.example.app02.ui.components.DemoBoldTextComponent
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshad
import com.example.app02.ui.theme.boxshadow
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    movieViewModel: MovieViewModel = viewModel(),
    cateViewModel: CateViewModel = viewModel(),

    ) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val cates = cateViewModel.cate.value
    var query by remember { mutableStateOf("") }

    val uiState by movieViewModel.uiState.collectAsState()
    val movies = uiState.movies
    CommonScaffold(isLogged, navController, loginViewModel, "user") { p ->
        LaunchedEffect(Unit) {
            movieViewModel.fetchMovies()
            cateViewModel.fetchCate()
            if (query.isNotBlank()) {
                movieViewModel.onSearchQueryChanged(query)
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(p), color = bcl
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()) // Thanh cuộn ngang
                        .padding(16.dp)
                ) {
                    cates.forEach { cate ->
                        Card(
                            modifier = Modifier
                                .weight(1f) // chia đều
                                .height(50.dp)
                                .padding(8.dp)
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
                                containerColor = Color.White.copy(alpha = 0.1f) // màu nền trắng mờ
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
                    query = query,
                    onQueryChange = {
                        query = it
                        movieViewModel.onSearchQueryChanged(it)
                    },
                    onSearch = { /* no-op */ },
                    searchResults = movies,
                    onResultClick = { id ->
                        navController.navigate("movie/${id}")
                        println("Clicked movie ID: $id")
                    }
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    item(span = { GridItemSpan(2) }) {
                        AutoSlidingCarousel(movies = movies, navController)
                    }
                    items(movies) { movie ->
                        MovieItem(movie, navController)
                    }


                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bcl)
            .customShadow(
                color = boxshadow,
                alpha = 0.2f,
                borderRadius = 16.dp,
                shadowRadius = 10.dp,
                offsetX = 4.dp,
                offsetY = 4.dp
            )
            .clip(RoundedCornerShape(16.dp))
            .padding(8.dp) // Thêm padding để tạo khoảng cách giữa bóng đổ và nội dung
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val painter = rememberAsyncImagePainter(
                model = if (movie.poster.startsWith("content://")) Uri.parse(movie.poster) else movie.poster
            )
            Image(
                painter = painter,
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("movieDetail/${movie.id}") }
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(12.dp))
            )
            DemoBoldTextComponent(value = movie.title)
            LightTextComponent(value = "Thể loại: ${movie.genre.name}")
            LightTextComponent(value = "Thời lượng: ${movie.duration} phút")
            Row {

                LightTextComponent(value = " ${movie.avg} ")
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "avg",
                    tint = Color.Yellow
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingCarousel(
    movies: List<Movie>,
    navController: NavController,
    modifier: Modifier = Modifier,
    autoScrollDuration: Long = 3000L
) {
    if (movies.isEmpty()) return

    val pagerState = rememberPagerState(initialPage = 0) { movies.size }
    val coroutineScope = rememberCoroutineScope()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    LaunchedEffect(pagerState) {
        while (true) {
            delay(autoScrollDuration)
            val nextPage = (pagerState.currentPage + 1) % movies.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(bcl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bcl)
                .height(screenHeight/3)
                .padding(8.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val movie = movies[page]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bcl)
                        .customShadow(
                            color = boxshadow,
                            alpha = 0.2f,
                            borderRadius = 16.dp,
                            shadowRadius = 10.dp,
                            offsetX = 4.dp,
                            offsetY = 4.dp
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                Image(
                    painter = rememberAsyncImagePainter(model = movie.poster),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("movieDetail/${movie.id}") }
//                        .height(screenHeight/3)
                        .clip(RoundedCornerShape(12.dp))
                )}}
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        PagerIndicator(pageCount = movies.size, currentPage = pagerState.currentPage)
    }
}

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(if (index == currentPage) 12.dp else 8.dp)
                    .background(
                        if (index == currentPage) Color.Black else Color.Gray,
                        shape = CircleShape
                    )
                    .padding(horizontal = 4.dp)
            )

        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreviewOfHomeScreen() {
//    val navController = rememberNavController() // Tạo NavController giả để tránh lỗi
////    HomeScreen(navController = navController)
//}