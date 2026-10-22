package com.example.app02.repositories

import com.example.app02.network.api.ApiService
import com.example.app02.network.api.MovieUpdateDTO
import com.example.app02.network.api.RetrofitInstance
import com.example.app02.models.Movie

class MovieRepository(private val apiService: ApiService = RetrofitInstance.apiService) {
    suspend fun getMovies() = apiService.getMovies()
    suspend fun getMoviesByCate(id: Int) = apiService.getMoviesByCate(id)
    suspend fun getMovieById(id: Int) = apiService.getMovieById(id)
    suspend fun deleteMovie(id: Int) = apiService.deleteMovie(id)
    suspend fun updateMovie(id: Int, updateDTO: MovieUpdateDTO) = apiService.updateMovie(id, updateDTO)
    suspend fun createMovie(movie: Movie) = apiService.createMovie(movie)
    suspend fun searchMovie(query: String) = apiService.searchMovie(query)
}