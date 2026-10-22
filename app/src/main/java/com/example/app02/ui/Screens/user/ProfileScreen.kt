package com.example.app02.ui.Screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.app02.data.DataStore
import com.example.app02.network.api.TicketDTO
import com.example.app02.ui.components.BackOnlyTopBar
import com.example.app02.ui.components.CommonScaffold
import com.example.app02.ui.components.CustomTabRow
import com.example.app02.ui.theme.bcl
import com.example.app02.viewmodel.LoginViewModel
import com.example.app02.viewmodel.TicketViewModel
import com.example.app02.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    dataStore: DataStore,
    loginViewModel: LoginViewModel,
    ticketViewModel: TicketViewModel,
    role: String
) {
    val isLogged by loginViewModel.isLogged.collectAsState(initial = false)
    val user by userViewModel.user.collectAsState()
    val tickets by ticketViewModel.tickets.collectAsState()

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val tabTitles = listOf("Thông tin", "Lịch sử vé")

    LaunchedEffect(Unit) {
        val id = dataStore.getUserIdFromToken()
        id?.let {
            userViewModel.fetchUserInfo(it)
            ticketViewModel.getTicketsByUser(it) {}
        }
    }

    CommonScaffold(isLogged, navController, loginViewModel, "users", showTopBar = false) { p ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(p)
        ) {
            BackOnlyTopBar(navController, title = "Hồ sơ")

            user?.let { currentUser ->
                CustomTabRow(
                    tabTitles = tabTitles,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index -> selectedTabIndex = index }
                )

                when (selectedTabIndex) {
                    0 -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF1B1B1B)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .background(Color(0xFFFFD54F), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = currentUser.name.firstOrNull()?.uppercase().toString(),
                                                color = Color(0xFF111111),
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = currentUser.name,
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "Thành viên",
                                            color = Color(0xFFBDBDBD),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFF151515)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "Thông tin tài khoản",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )

                                        ProfileInfoRow(
                                            label = "Tên tài khoản",
                                            value = currentUser.name,
                                            icon = Icons.Default.Person
                                        )
                                        ProfileInfoRow(
                                            label = "Email",
                                            value = currentUser.email,
                                            icon = Icons.Default.Email
                                        )
                                        ProfileInfoRow(
                                            label = "Số điện thoại",
                                            value = currentUser.phone,
                                            icon = Icons.Default.Phone
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        if (tickets == null) {
                            LoadingIndicator()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = "Lịch sử vé",
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                items(tickets) { ticket ->
                                    TicketCard(ticket)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(Color(0xFF2A2A2A), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFFD54F),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color(0xFFBDBDBD),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TicketCard(ticket: TicketDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(Color(0xFF252525), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF3A3A3A), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = ticket.qr),
                    contentDescription = "QR vé",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = ticket.movieName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ngày thanh toán: ${ticket.payTime}",
                    color = Color(0xFFBEBEBE),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Ghế: ${ticket.seat.joinToString { "${it.row}${it.col}" }}",
                    color = Color(0xFFBEBEBE),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Tổng tiền: ${ticket.total} đ",
                    color = Color(0xFFFFD54F),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

