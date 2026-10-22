package com.example.app02.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app02.network.api.MovieUpdateDTO
import com.example.app02.models.Movie
import com.example.app02.repositories.MovieRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

// State cho danh sách phim & màn hình chính
data class MovieUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val selectedMovie: Movie? = null,
    val userMessage: String? = null, // Dùng để hiển thị thông báo trong dialog
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchMovies()

        // Kích hoạt debounce search tự động
        viewModelScope.launch {
            _searchQuery.debounce(300).distinctUntilChanged().collect { query ->
                if (query.isBlank()) {
                    fetchMovies()
                } else {
                    search(query)
                }
            }
        }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getMovies()
                _uiState.update { it.copy(isLoading = false, movies = response) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi khi tải danh sách: ${e.message}"
                    )
                }
            }
        }
    }

    fun fetchMoviesByCate(categoryId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getMoviesByCate(categoryId)
                _uiState.update { it.copy(isLoading = false, movies = response) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi khi lọc phim: ${e.message}"
                    )
                }
            }
        }
    }

    fun fetchMovieDetails(id: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    selectedMovie = null,
                    errorMessage = null
                )
            }
            try {
                val response = repository.getMovieById(id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        selectedMovie = response
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Lỗi tải chi tiết phim: ${e.message}"
                    )
                }
            }
        }
    }

    fun deleteMovie(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.deleteMovie(id)
                if (response.isSuccessful) {
                    val msg = response.body()?.string() ?: "Xóa phim thành công"
                    // Xóa item thành công -> Cập nhật lại list trong UI State mà không cần gọi lại API
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            movies = state.movies.filterNot { it.id == id },
                            userMessage = msg
                        )
                    }
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Xóa phim thất bại"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi mạng khi xóa: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateMovie(id: Int, updateDTO: MovieUpdateDTO) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }
            try {
                val response = repository.updateMovie(id, updateDTO)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false, userMessage = response.body()?.string()
                                ?: "Cập nhật phim thành công"
                        )
                    }
                    fetchMovieDetails(id)

                    fetchMovies() // Refresh danh sách sau khi update
                } else {
                    val errorMsg = response.errorBody()?.string()
                        ?: "Cập nhật phim thất bại (HTTP \${response.code()})"
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi mạng khi cập nhật: ${e.message}"
                    )
                }
            }
        }
    }

    fun createMovie(movie: Movie) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.createMovie(movie)
                if (response.isSuccessful) {
                    val msg = response.body()?.string() ?: "Thêm phim mới thành công"
                    _uiState.update { it.copy(isLoading = false, userMessage = msg) }
                    fetchMovies()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Thêm phim mới thất bại"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi mạng: ${e.message}"
                    )
                }
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.searchMovie(query) ?: emptyList()
                _uiState.update { it.copy(isLoading = false, movies = response) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false, errorMessage = "Lỗi tìm kiếm: ${e.message}"
                    )
                }
            }
        }
    }

    // Hàm gọi từ UI sau khi đã hiển thị dialog để reset thông báo
    fun userMessageShown() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun errorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
