package com.example.app02.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.data.DataStore
import com.example.app02.network.api.ForgotPasswordRequest
import com.example.app02.network.api.ResetPasswordRequest
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()


    private val dataStore = DataStore(application)
    fun fetchUserInfo(id: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.getUser(id)
                _user.value = response
                Log.d("BillScreen", "Tên user: ${response.name}")

            }catch (E: Exception){
                Log.e("UserViewModel", "Loi khi lay thong tin", E)
            }
        }
    }
    fun forgotPassword(email: String, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val dto = ForgotPasswordRequest(email)
                val response = RetrofitInstance.apiService.forgotPassword(dto)
                if (response.isSuccessful){
                    onResult(true)
                }
                onResult(false)
            }catch (e: Exception){
                onResult(false)
            }
        }
    }

    fun resetPassword(reset: ResetPasswordRequest, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.apiService.resetPassword(reset)
                if (response.isSuccessful){
                    onResult(true)
                }
                onResult(false)
            }catch (e: Exception){
                onResult(false)
            }
        }
    }

}