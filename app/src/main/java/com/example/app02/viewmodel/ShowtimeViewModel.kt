package com.example.app02.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.api.ScheduleMovieRequest
import com.example.app02.network.api.ScheduleRequest
import com.example.app02.models.Showtime
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.cancellation.CancellationException

class ShowtimeViewModel() : ViewModel() {

    private val _showtimes = MutableStateFlow<List<Showtime>>(emptyList())
    val showtimes = _showtimes.asStateFlow()
    private val _showtime = MutableStateFlow<Showtime?>(null)

    val showtime = _showtime.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _hasLoadedShowtimes = MutableStateFlow(false)
    val hasLoadedShowtimes = _hasLoadedShowtimes.asStateFlow()

    fun fetchShowtimes(movieId: Int, date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasLoadedShowtimes.value = false

            try {

                val response = RetrofitInstance.apiService.getShowtime(movieId, date)
                _showtimes.value = response
                _hasLoadedShowtimes.value = true

            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Không thể tải danh sách suất chiếu. Vui lòng thử lại."
            } finally {
                _isLoading.value = false
            }
        }
        ; }

    fun fetchShowtimeById(showtimeId: Int) {
        viewModelScope.launch {
            ; try {
            val fetchedShowtime = RetrofitInstance.apiService.getShowtimeById(showtimeId)
            _showtime.value = fetchedShowtime
        } catch (e: Exception) {
            e.printStackTrace()
        }
        }; }

    fun scheduleByMovie(
        movieId: Int,
        fromDate: LocalDate,
        toDate: LocalDate,
        dailyStart: LocalTime,
        dailyEnd: LocalTime,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            ; try {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val req = ScheduleMovieRequest(
                movieId = movieId,
                fromDate = fromDate.toString(),
                toDate = toDate.toString(),
                dailyStart = dailyStart.format(timeFormatter),
                dailyEnd = dailyEnd.format(timeFormatter)
            )

            val response = RetrofitInstance.apiService.scheduleByMovie(req)

            if (response.isSuccessful) {
                Log.d("Showtime", "Lên lịch chiếu thành công")
                onResult(true)
            } else {
                Log.e(
                    "Showtime",
                    "Lên lịch thất bại: ${response.code()} - " +
                            "${response.errorBody()?.string()}"
                )
                onResult(false)
            }
        } catch (e: Exception) {
            Log.e("Showtime", "Lỗi khi lên lịch chiếu", e)
            onResult(false)
        }
        }; }
}
