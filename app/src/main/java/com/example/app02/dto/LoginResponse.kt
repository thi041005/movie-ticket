package com.example.app02.dto

//
//data class LoginResponse(
//    val token: String? = null,
//    val errorMessage: String? = null
//
//)
data class LoginResponse(val token: String, val userId: Int, val role: String)
