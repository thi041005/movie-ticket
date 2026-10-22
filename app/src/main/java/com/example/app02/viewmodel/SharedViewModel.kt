package com.example.app02.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.models.Seat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    private val _selectedSeat = MutableStateFlow<List<Seat>>(emptyList())
    val selectedSeat = _selectedSeat.asStateFlow()

    fun getSelectedSeats(seats: List<Seat>){
        viewModelScope.launch {
            _selectedSeat.value = seats
        }
    }
}
//data class BookState(
//    val isLoader: Boolean = false,
//    val msg: String? = null,
//    val err: String? = null
//)