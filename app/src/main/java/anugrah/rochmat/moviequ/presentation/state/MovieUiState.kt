package anugrah.rochmat.moviequ.presentation.state

import androidx.paging.PagingData
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList
import kotlinx.coroutines.flow.Flow

data class MovieUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val popularMovies: MovieList? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val usePaging3: Boolean = false,
    val pagingDataFlow: Flow<PagingData<Movie>>? = null
)