package com.example.app02.network.api

import com.example.app02.dto.HoldSeatReq
import com.example.app02.dto.LoginRequest
import com.example.app02.dto.LoginResponse
import com.example.app02.dto.SignupRequest
import com.example.app02.models.Cate
import com.example.app02.models.Movie
import com.example.app02.models.Seat
import com.example.app02.models.Genre
import com.example.app02.models.Showtime
import com.example.app02.models.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signup(@Body registerRequest: SignupRequest): Response<SignupResponse>

    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: Int?): User

    @GET("/public/movies")
    suspend fun getMovies(): List<Movie>

    @GET("/public/cate")
    suspend fun getCategories(): List<Cate>

    @GET("/public/movie/{id}")
    suspend fun getMovieById(@Path("id") id: Int): Movie

    @GET("/public/movies/withCate/{id}")
    suspend fun getMoviesByCate(@Path("id") id: Int): List<Movie>

    @GET("/public/movies/search")
    suspend fun searchMovie(@Query("query") query: String): List<Movie>

    @GET("user/showtime/{movieId}/{dates}")
    suspend fun getShowtime(
        @Path("movieId") movieId: Int,
        @Path("dates") date: String
    ): List<Showtime>

    @GET("user/showtime/{showtimeId}")
    suspend fun getShowtimeById(@Path("showtimeId") showtimeId: Int): Showtime

    @GET("user/seat/get")
    suspend fun getSeats(
        @Query("showtimeId") showtimeId: Int,
        @Query("userId") userId: Int
    ): List<Seat>

    @POST("user/seat/hold")
    suspend fun holdSeat(@Body request: HoldSeatReq): Response<ResponseBody>

    @POST("user/seat/cancel-hold")
    suspend fun cancelHoldSeat(@Body request: HoldSeatReq): Response<ResponseBody>

    @POST("user/seat/cancel-all")
    suspend fun cancelAllHold(@Body request: AllSeatHeld): Response<ResponseBody>

    @POST("user/book")
    suspend fun book(@Body request: BookingRequest): Response<BookingResponse>

    @POST("user/cancel-book")
    suspend fun cancelBook(@Body req: CancelBookReq): Response<ResponseBody>

    @GET("user/booking/{id}/state")
    suspend fun getBookingState(@Path("id") bookingId: Int): Response<Int>

    @GET("user/ticket/{id}")
    suspend fun getTicket(@Path("id") bookingId: Int): Response<TicketDTO>

    @GET("admin/revenue/daily/{from}/{to}")
    suspend fun getRevenueDaily(
        @Path("from") from: String,
        @Path("to") to: String
    ): List<RevenueDTO>

    @POST("admin/movie/delete/{id}")
    suspend fun deleteMovie(@Path("id") id: Int): Response<ResponseBody>

    @PUT("admin/movie/update/{id}")
    suspend fun updateMovie(
        @Path("id") id: Int,
        @Body movie: MovieUpdateDTO
    ): Response<ResponseBody>

    @POST("admin/movie/add")
    suspend fun createMovie(@Body movie: Movie): Response<ResponseBody>

    @GET("user/rate/{id}")
    suspend fun getRate(@Path("id") id: Int): List<RatingResponseDTO>

    @POST("user/rating")
    suspend fun rating(@Body ratingDTO: RatingDTO): Response<ResponseBody>

    @GET("user/tickets")
    suspend fun getTicketsByUser(@Query("id") id: Int): List<TicketDTO>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ResponseBody>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResponseBody>

    @POST("public/schedule-movie")
    suspend fun scheduleByMovie(@Body req: ScheduleMovieRequest): Response<ResponseBody>

    @GET("public/count-seat/{showtimeId}")
    suspend fun countSeat(@Path("showtimeId") showtimeId: Int): Response<Int?>
}

data class ScheduleRequest(
    val fromDate: String,
    val toDate: String,
    val dailyStart: String,
    val dailyEnd: String

)

data class ScheduleMovieRequest(
    val movieId: Int,
    val fromDate: String,
    val toDate: String,
    val dailyStart: String,
    val dailyEnd: String
)


data class MovieSearch(
    val id: Int,
    val title: String,
    val cast: String,
    val poster: String
)

data class SignupResponse(
    val message: String,
)

data class AllSeatHeld(
    val userId: Int,
    val showtimeId: Int
)

data class BookingRequest(
    val userId: Int,
    val showtimeId: Int,
    val seatIds: List<String>,
    val total: Double
)

data class BookingResponse(
    val msg: String,
    val bookingId: Int,
    val qrUrl: String
)

data class CancelBookReq(
    val bookingId: Int,
    val userId: Int
)

data class TicketDTO(
    val movieName: String,
    val audName: String,
    val ticketId: Int,
    val payTime: String,
    val total: Double,
    val qr: String,
    val seat: List<SeatInfo>
)


data class SeatInfo(
    val row: String,
    val col: Int
)

data class RevenueDTO(
    val date: String,
    val total: Double
)

data class MovieUpdateDTO(
    val des: String? = null,
    val title: String? = null,
    val genreId: Int? = null,
    val duration: Int? = null,
    val poster: String? = null,
    val cast: String? = null,
    val director: String? = null,
)


data class MovieDTO(
    val id: Int,
    val des: String,
    val title: String,
    val trailer: String,
    val genre: Genre?,
    val category: Cate?,
    val duration: Int,
    val poster: String,
    val cast: String,
    val director: String,
    val startDate: LocalDate,
    val endDate: LocalDate
)


data class RatingDTO(
    val userId: Int,
    val movieId: Int,
    val score: Int,
    val comment: String?
)

data class RatingResponseDTO(
    val userId: Int,
    val username: String,
    val score: Int,
    val comment: String,
    val ratedAt: String
)


data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
