package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.api.TicketDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TicketViewModel(application: Application) : AndroidViewModel(application) {

    val dataStore = DataStore(application)

    private val _ticketUiState =
        MutableStateFlow<TicketUiState>(TicketUiState.Loading)

    val ticketUiState = _ticketUiState.asStateFlow()

    private val _tickets =
        MutableStateFlow<List<TicketDTO>>(emptyList())

    val tickets = _tickets.asStateFlow()

    private var getTicketJob: Job? = null

    fun getTicket(
        bookingId: Int,
        maxAttempts: Int = 8,
        retryDelayMs: Long = 1_000L
    ) {
        // Không để request cũ ghi đè kết quả của booking mới.
        getTicketJob?.cancel()

        getTicketJob = viewModelScope.launch {
            _ticketUiState.value = TicketUiState.Loading

            repeat(maxAttempts) { attempt ->
                try {
                    val response =
                        RetrofitInstance.apiService.getTicket(bookingId)

                    if (response.isSuccessful) {
                        val body = response.body()

                        if (body != null) {
                            _ticketUiState.value =
                                TicketUiState.Success(body)
                            return@launch
                        }

                        // HTTP thành công nhưng body chưa có.
                        Log.w(
                            "TicketViewModel",
                            "Booking $bookingId: response body rỗng"
                        )
                    } else {
                        val code = response.code()

                        Log.w(
                            "TicketViewModel",
                            "Booking $bookingId: HTTP $code"
                        )

                        // 404 có thể là vé chưa được tạo xong.
                        // 5xx có thể là lỗi tạm thời từ server.
                        if (code != 404 && code !in 500..599) {
                            _ticketUiState.value = TicketUiState.Error(
                                "Không thể tải vé (HTTP $code). " +
                                        "Vui lòng thử lại."
                            )
                            return@launch
                        }
                    }
                } catch (e: kotlinx.coroutines.CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(
                        "TicketViewModel",
                        "Lỗi tải vé booking $bookingId",
                        e
                    )
                }

                // Chờ trước lần thử tiếp theo, trừ lần cuối.
                if (attempt < maxAttempts - 1) {
                    delay(retryDelayMs)
                }
            }

            // Sau 8 lần thử vẫn chưa có vé.
            // Không khẳng định vé đã bị hủy vì chưa đủ bằng chứng.
            _ticketUiState.value = TicketUiState.NotFound
        }
    }

    fun retryGetTicket(bookingId: Int) {
        getTicket(bookingId)
    }

    fun getTicketsByUser(id:Int,onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val response = RetrofitInstance.apiService.getTicketsByUser(userId)
                    _tickets.value = response
                    onResult(true)
                } catch (e: Exception) {
                    onResult(false)
                    Log.e("TicketViewModel", "${e.message}")

                }
            }
            onResult(false)

        }
    }
}
sealed interface TicketUiState {
    data object Loading : TicketUiState

    data class Success(
        val ticket: TicketDTO
    ) : TicketUiState

    data object NotFound : TicketUiState

    data class Error(
        val message: String
    ) : TicketUiState
}
