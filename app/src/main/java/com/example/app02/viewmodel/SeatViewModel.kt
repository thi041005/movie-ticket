package com.example.app02.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.dto.HoldSeatReq
import com.example.app02.network.api.AllSeatHeld
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.models.Seat
import com.example.app02.network.api.NetworkConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class SeatViewModel(application: Application) : AndroidViewModel(application) {
    private var webSocket: WebSocket? = null
    private val _seatUpdates = MutableStateFlow<SeatStatusUpdate?>(null)
    val seatUpdates = _seatUpdates.asStateFlow()


    fun initWebSocket(showtimeId: Int) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(NetworkConfig.WS_URL)
            .build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val update = Gson().fromJson(text, SeatStatusUpdate::class.java)
                if (update.showtimeId == showtimeId) {
                    _seats.update { current ->
                        current.map {
                            if (it.id == update.seatId) it.copy(state = update.state)
                            else it
                        }
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("failed: ${t.message}")
            }
        })


    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "viewModel destroyed")
    }

    private val _seats = MutableStateFlow<List<Seat>>(emptyList())
    val seats = _seats.asStateFlow()

    val dataStore = DataStore(application)
    private val _seatState = MutableStateFlow<SeatState>(SeatState.Loading)
    val seatState: StateFlow<SeatState> = _seatState


    fun fetchSeats(showtimeId: Int) {
        viewModelScope.launch {
            try {
                val userId = dataStore.getUserIdFromToken()
                userId?.let {
                    val response = RetrofitInstance.apiService.getSeats(showtimeId, userId)
                    _seats.value = response
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun holdSeat(seatId: Int, showtimeId: Int, onResult: (Boolean) -> Unit) {
        _seatState.value = SeatState.Loading
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val response = RetrofitInstance.apiService.holdSeat(
                        HoldSeatReq(
                            seatId,
                            userId,
                            showtimeId
                        )
                    )
                    if (response.isSuccessful) {
                        val message = response.body()?.string() ?: "Giữ ghế thành công"
                        _seatState.value = SeatState.Success(message)
                        onResult(true)
                    } else {
                        val errorMessage =
                            response.errorBody()?.string() ?: "Ghế đang được giữ bởi người khác"
                        _seatState.value = SeatState.Err(errorMessage)
                        onResult(false)
                    }
                } catch (e: Exception) {
                    _seatState.value = SeatState.Err("Lỗi hệ thống: ${e.message}")
                    onResult(false)
                }
            } ?: run {
                _seatState.value = SeatState.Err("Không xác định được người dùng")
                onResult(false)
            }
        }
    }

    fun cancelHold(seatId: Int, showtimeId: Int, onResult: (Boolean) -> Unit) {
        _seatState.value = SeatState.Loading
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val response = RetrofitInstance.apiService.cancelHoldSeat(
                        HoldSeatReq(
                            seatId,
                            userId,
                            showtimeId
                        )
                    )
                    if (response.isSuccessful) {
                        val msg = response.body()?.string() ?: "Đã bỏ chọn ghế"
                        _seatState.value = SeatState.Success(msg)
                        onResult(true)
                    } else {
                        _seatState.value = SeatState.Err(
                            response.errorBody()?.string() ?: "Ghế đang được giữ bởi người "
                        )
                        onResult(false)
                    }
                } catch (e: Exception) {
                    _seatState.value = SeatState.Err("loi he thong ${e.message}")
                }
            } ?: run {
                _seatState.value = SeatState.Err("khong xac dinh duoc nguoi dung")
            }
        }
    }

    fun cancel(showtimeId: Int, onResult: (Boolean) -> Unit) {
        _seatState.value = SeatState.Loading
        viewModelScope.launch {
            val userId = dataStore.getUserIdFromToken()
            userId?.let {
                try {
                    val response =
                        RetrofitInstance.apiService.cancelAllHold(AllSeatHeld(userId, showtimeId))
                    if (response.isSuccessful) {

                        onResult(true)
                        _seatState.value =
                            SeatState.Success(response.body()?.string() ?: "Da xoa het tat ca")
                    } else
                        onResult(false)
                } catch (e: Exception) {
                    _seatState.value = SeatState.Err("loi ${e.message}")
                }
            } ?: run {
                _seatState.value = SeatState.Err("Khong xac dinh duoc nguoi dung")
            }
        }
    }
}

sealed class SeatState {
    object Loading : SeatState()
    data class Success(val msg: String) : SeatState()
    data class Err(val errMsg: String) : SeatState()
}

data class SeatStatusUpdate(
    val seatId: Int,
    val showtimeId: Int,
    val state: Int,
    val userId: Int
)