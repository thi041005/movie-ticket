package com.example.app02.nav

sealed class Screen(val route: String) {
     data object UserHome : Screen("user_home")
     data object Bill : Screen("bill")
     data object Login : Screen("login")
     data object SignUp : Screen("signup")
     data object Reset : Screen("reset")
     data object MovieDetail : Screen("movieDetail/{id}")
     data object Conditions : Screen("conditions")
     data object ShowSeat : Screen("showSeat/{showtimeId}")
     data object Payment: Screen("payment/{bookingId}/{qrUrl}")
     data object Ticket: Screen("ticket")
     data object AdminHome: Screen("admin_home")
     data object MovieManage: Screen("movie_manage")
     data object MovieDailAdmin: Screen("movie/{id}")
     data object TicketLayout: Screen("ticket_layout")
     data object Showtime: Screen("showtime")
     data object ShowtimeDetail: Screen("showtime-detail")
     data object CreateShowtime: Screen("create_showtime/{id}")
}
