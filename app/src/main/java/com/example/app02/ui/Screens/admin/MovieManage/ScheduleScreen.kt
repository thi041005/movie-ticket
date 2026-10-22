package com.example.app02.ui.Screens.admin.MovieManage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.models.Cate
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.components.CustomizableSearchBar
import com.example.app02.ui.components.LightTextComponent
import com.example.app02.ui.components.customShadow
import com.example.app02.ui.theme.bcl
import com.example.app02.ui.theme.boxshad
import com.example.app02.ui.theme.boxshadow
import com.example.app02.viewmodel.CateViewModel
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel
import com.example.app02.viewmodel.ShowtimeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    cateViewModel: CateViewModel = viewModel(),
    movieViewModel: MovieViewModel = viewModel()
) {
    val cate = cateViewModel.cate.value
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val tabTitles = listOf("Theo dõi ghế")
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        movieViewModel.fetchMovies()
        cateViewModel.fetchCate()
    }
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

                0 -> MovieListSec(cate, navController, movieViewModel)
            }
        }
    }
}

@Composable
fun MovieListSec(
    cates: List<Cate>,
    navController: NavController,
    movieViewModel: MovieViewModel,
) {
    var query by remember { mutableStateOf("") }
    val uiState by movieViewModel.uiState.collectAsState()

    val movies = uiState.movies
    val result = uiState.movies
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            movieViewModel.search(query)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
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
                    containerColor = Color.White.copy(alpha = 0.1f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
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
            movieViewModel.search(it)
        },
        onSearch = { /* no-op */ },
        searchResults = result,
        onResultClick = { id ->
            navController.navigate("movie/${id}")
            println("Clicked movie ID: $id")
        }
    )
    LazyColumn {
        items(movies) { movie ->
            Box( //2
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
                        navController.navigate("showtime-detail/${movie.id}")
                    }
            )
            {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                    ,
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = boxshadow
                    )

                ) {
                    Row {
                        Box(modifier = Modifier.weight(1f)) {
                            Image(
                                painter = rememberAsyncImagePainter(model = movie.poster),
                                contentDescription = "Movie Poster",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .size(400.dp)
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
                                androidx.compose.material.Text(
                                    movie.title,
                                    style = TextStyle(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.LightGray
                                    )
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                LightTextComponent(movie.title)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

