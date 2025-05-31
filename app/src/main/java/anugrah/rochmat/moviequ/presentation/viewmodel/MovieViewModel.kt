package anugrah.rochmat.moviequ.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import anugrah.rochmat.moviequ.domain.usecase.GetPopularMoviesUseCase
import anugrah.rochmat.moviequ.presentation.state.MovieUiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MovieViewModel(
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _uiState = MutableLiveData(MovieUiState())
    val uiState: LiveData<MovieUiState> = _uiState

    init {
        loadPopularMovies()
    }

    fun loadPopularMovies() {
        _uiState.value = _uiState.value?.copy(isLoading = true, error = null)

        val popularMovieDisposable = getPopularMoviesUseCase()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { movieList ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        popularMovies = movieList,
                        error = null
                    )
                },
                { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        error = error.message ?: "Unknown Error Occured"
                    )
                }
            )

        compositeDisposable.add(popularMovieDisposable)
    }

    fun clearError() {
        _uiState.value = _uiState.value?.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}