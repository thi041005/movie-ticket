package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.BookingRequest
import com.example.app02.network.api.BookingResponse
import com.example.app02.network.api.CancelBookReq
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.models.Seat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(application: Application) : AndroidViewModel(application) {
    var selectedSeats: List<Seat> by mutableStateOf(emptyList())

    private val _bookingState = MutableStateFlow<BookState>(BookState.Loading)
    val bookingState: StateFlow<BookState> = _bookingState
    val dataStore = DataStore(application)

    private val _counted = MutableStateFlow<Int?>(0)
    val counted: StateFlow<Int?> = _counted.asStateFlow()

    private val _state = MutableStateFlow<Int?>(0)
    val state: StateFlow<Int?> = _state.asStateFlow()
    fun bookSeat(
        showtimeId: Int,
        seatIds: List<String>,
        total: Double,
        onResult: (BookingResponse?) -> Unit
    ) {

        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val request = BookingRequest(userId, showtimeId, seatIds, total)
                    val response = RetrofitInstance.apiService.book(request)
                    if (response.isSuccessful) {
                        val booking = response.body()
                        onResult(booking)
                    } else {
                        onResult(null)
                    }


                } catch (e: Exception) {
                    onResult(null)
                    e.printStackTrace()
                }
            } ?: run {
                onResult(null)
            }
        }
    }

    fun cancelBook(bookingId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val request = CancelBookReq(bookingId, userId)
                    val response = RetrofitInstance.apiService.cancelBook(request)
                    if (response.isSuccessful) {
                        val msg = response.body()?.string() ?: "Đã hủy thanh toán!"
                        _bookingState.value = BookState.Success(msg)
                        onResult(true)
                    } else {
                        _bookingState.value = BookState.Err(
                            response.errorBody()?.string() ?: "Không thể hủy thanh toán"
                        )
                        onResult(false)
                    }
                } catch (e: Exception) {
                    _bookingState.value = BookState.Err("loi he thong ${e}")
                    onResult(false)
                }
            } ?: run {
                _bookingState.value = BookState.Err("khong xac dinh duoc nguoi dung")
                onResult(false)

            }
        }

    }


    suspend fun refreshBookingState(bookingId: Int): Int? {
        return try {
            val response =
                RetrofitInstance.apiService.getBookingState(bookingId)

            if (response.isSuccessful) {
                response.body()?.also { bookingStatus ->
                    _state.value = bookingStatus
                }
            } else {
                Log.e(
                    "BookingViewModel",
                    "Không thể kiểm tra booking: HTTP ${response.code()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e(
                "BookingViewModel",
                "Lỗi kiểm tra trạng thái booking $bookingId",
                e
            )
            null
        }
    }

    fun getBookingState(bookingId: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val status = refreshBookingState(bookingId)
            onResult(status != null)
        }
    }

    fun countSeat(showtimeId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.countSeat(showtimeId)
                if (response.isSuccessful) {
                    _counted.value = response.body() ?: 0
                }
            } catch (e: Exception) {
                Log.e("dem ghe loi ", "${e}")
            }
        }
    }

}


sealed class BookState {
    object Loading : BookState()
    data class Success(val msg: String) : BookState()
    data class Err(val errMsg: String) : BookState()
}