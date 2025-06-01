package anugrah.rochmat.moviequ.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.usecase.GetPopularMoviesPagingUseCase
import anugrah.rochmat.moviequ.domain.usecase.GetPopularMoviesUseCase
import anugrah.rochmat.moviequ.presentation.state.MovieUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MovieViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getPopularMoviesPagingUseCase: GetPopularMoviesPagingUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val allMovies = mutableListOf<Movie>()

    private val _uiState = MutableLiveData(MovieUiState())
    val uiState: LiveData<MovieUiState> = _uiState

    init {
        loadPopularMovies()
    }

    fun loadPopularMovies(isRefresh: Boolean = false) {
        val currentState = _uiState.value ?: MovieUiState()

        if (isRefresh) {
            allMovies.clear()
            _uiState.value = currentState.copy(
                isRefreshing = true,
                error = null,
                currentPage = 1,
                usePaging3 = false
            )
        } else {
            _uiState.value = currentState.copy(
                isLoading = currentState.currentPage == 1,
                isLoadingMore = currentState.currentPage > 1,
                error = null,
                usePaging3 = false
            )
        }

        val pageToLoad = if (isRefresh) 1 else currentState.currentPage
        val moviesDisposable = getPopularMoviesUseCase.invoke(pageToLoad)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { movieList ->
                    handleMoviesSuccess(movieList, isRefresh)
                },
                { error ->
                    handleMoviesError(error)
                }
            )

        compositeDisposable.add(moviesDisposable)
    }

    fun loadMoreMovies() {
        val currentState = _uiState.value ?: MovieUiState()

        if (currentState.isLoading ||
            currentState.isLoadingMore ||
            !currentState.hasMorePages ||
            currentState.usePaging3) {
            return
        }

        _uiState.value = currentState.copy(
            currentPage = currentState.currentPage +1
        )
        loadPopularMovies()
    }

    fun loadMoviesWithPaging3() {
        // Clear manual pagination state
        allMovies.clear()

        _uiState.value = _uiState.value?.copy(
            isLoading = true,
            isRefreshing = false,
            isLoadingMore = false,
            error = null,
            usePaging3 = true,
            popularMovies = null,  // Clear manual data
            currentPage = 1,
            hasMorePages = true
        )

        val pagingDisposable = getPopularMoviesPagingUseCase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { pagingDataFlow ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        pagingDataFlow = pagingDataFlow,
                        error = null,
                        usePaging3 = true
                    )
                },
                { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown Error Occurred",
                        usePaging3 = true
                    )
                }
            )

        compositeDisposable.add(pagingDisposable)
    }

    fun switchToPaging3() {
        loadMoviesWithPaging3()
    }

    fun switchToManualPagination() {
        // Clear Paging 3 state
        _uiState.value = _uiState.value?.copy(
            usePaging3 = false,
            pagingDataFlow = null,
            currentPage = 1,
            hasMorePages = true
        )
        loadPopularMovies(isRefresh = true)  // Fresh start with manual pagination
    }

    private fun handleMoviesSuccess(movieList: MovieList, isRefresh: Boolean) {
        if (isRefresh) allMovies.clear()

        allMovies.addAll(movieList.movies)
        val updatedMovieList = MovieList(
            page = movieList.page,
            movies = allMovies.toList(),
            totalPages = movieList.totalPages,
            totalResult = movieList.totalResult
        )

        _uiState.value = _uiState.value?.copy(
            isLoading = false,
            isRefreshing = false,
            isLoadingMore = false,
            popularMovies = updatedMovieList,
            error = null,
            hasMorePages = movieList.page < movieList.totalPages,
            usePaging3 = false  // 🔄 Ensure we're in manual mode
        )
    }

    private fun handleMoviesError(error: Throwable) {
        _uiState.value = _uiState.value?.copy(
            isLoading = false,
            isRefreshing = false,
            isLoadingMore = false,
            error = error.message ?: "Unknown Error Occurred"
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}