package com.example.app02.ui.Screens.admin.MovieManage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app02.network.api.MovieUpdateDTO
import com.example.app02.models.Movie
import com.example.app02.models.DialogData
import com.example.app02.ui.components.Btn
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CommonAlertDialog
import com.example.app02.ui.components.MyTextField
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.MovieViewModel

@Composable
fun MovieDetailScreen(
    id: Int,
    navController: NavController,
    movieViewModel: MovieViewModel,
    loginViewModel: LoginViewModel
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val uiState by movieViewModel.uiState.collectAsState()
    var dialogData by remember { mutableStateOf<DialogData?>(null) }

    LaunchedEffect(id) {
        movieViewModel.fetchMovieDetails(id)

    }
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            dialogData = DialogData(
                title = "Thành công",
                message = msg,
                dismissText = null,
                icon = Icons.Default.CheckCircle,
                onConfirm = {
                    dialogData = null
                    movieViewModel.userMessageShown()
                    if (msg.contains("Xóa", ignoreCase = true)) {
                        navController.popBackStack()
                    }
                },
                onDismiss = {
                    dialogData = null
                    movieViewModel.userMessageShown()
                }
            )
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            dialogData = DialogData(
                title = "Có lỗi xảy ra",
                message = error,
                dismissText = null,
                icon = Icons.Default.ErrorOutline,
                onConfirm = {
                    dialogData = null
                    movieViewModel.errorMessageShown()
                },
                onDismiss = {
                    dialogData = null
                    movieViewModel.errorMessageShown()
                }
            )
        }
    }

    dialogData?.let { CommonAlertDialog(it) }


    CommonScaffold(isLogged, navController, loginViewModel, "admin", showTopBar = false) { p ->
        Box(
            Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            val movie = uiState.selectedMovie
            movie?.let {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF111111))
                        .padding(p)
                ) {
                    com.example.app02.ui.components.BackOnlyTopBar(navController, title = it.title)
                    MovieDetailContent(id, it, movieViewModel)
                }
            }

        }

    }

}

@Composable
fun MovieDetailContent(
    id: Int,
    movie: Movie,
    movieViewModel: MovieViewModel,
) {

    var des by remember(movie.des) { mutableStateOf(movie.des ?: "") }
    var durationText by remember(movie.duration) {
        mutableStateOf(
            movie.duration.toString() ?: "0"
        )
    }
    var director by remember(movie.director) { mutableStateOf(movie.director ?: "") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1B1B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MyTextField(
                    "Mô tả",
                    leadingIconVector = Icons.Default.Edit,
                    des,
                    onValueChange = { des = it }
                )
                MyTextField(
                    "Thời lượng",
                    leadingIconVector = Icons.Default.Edit,
                    durationText,
                    onValueChange = { durationText = it }
                )
                MyTextField(
                    "Đạo diễn",
                    leadingIconVector = Icons.Default.Edit,
                    director,
                    onValueChange = { director = it }
                )
            }
        }

        Btn("Cập nhật", onClick = {
            val parseDuration = durationText.toIntOrNull() ?: movie.duration ?: 0
            val movieUpdate = MovieUpdateDTO(des, duration = parseDuration, director = director)
            movieViewModel.updateMovie(id, movieUpdate)
        })

        Button(
            onClick = {
                movieViewModel.deleteMovie(id)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteForever,
                contentDescription = "delete",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Xóa phim", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
