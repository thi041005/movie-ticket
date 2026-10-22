package com.example.app02.models

data class Showtime(
    val id: Int,
    val movie: Movie,
    val auditorium: Auditorium,
    val startTime: List<Int>,
    val endTime: List<Int>
)
