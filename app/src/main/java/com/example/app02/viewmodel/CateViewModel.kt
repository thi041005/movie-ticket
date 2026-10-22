package com.example.app02.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.models.Cate
import kotlinx.coroutines.launch

class CateViewModel: ViewModel(){
    val cate = mutableStateOf<List<Cate>>(emptyList())

    fun fetchCate(){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getCategories()
                cate.value = response
            }catch (e: Exception){
                println("loi ${e.message}")
            }
        }
    }
}