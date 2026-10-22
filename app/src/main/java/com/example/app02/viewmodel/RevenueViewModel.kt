package com.example.app02.viewmodel

import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.network.api.RevenueDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class RevenueViewModel : ViewModel() {

    private val _revenues = MutableStateFlow<List<RevenueDTO>>(emptyList())
    val revenues = _revenues.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _hasLoadedRevenue = MutableStateFlow(false)
    val hasLoadedRevenue = _hasLoadedRevenue.asStateFlow()

    fun fetchRevenues() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasLoadedRevenue.value = false

            try {
                val response = RetrofitInstance.apiService.getRevenueDaily(LocalDate.now().minusDays(7).toString(), LocalDate.now().toString())
                _revenues.value = response
                _hasLoadedRevenue.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Không thể tải danh sách suất chiếu. Vui lòng thử lại."
            }finally {
                _isLoading.value = false
            }
        }
    }
}
