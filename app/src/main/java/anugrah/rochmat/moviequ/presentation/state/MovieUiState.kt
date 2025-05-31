package anugrah.rochmat.moviequ.presentation.state

import anugrah.rochmat.moviequ.domain.entity.MovieList

data class MovieUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val popularMovies: MovieList? = null
)