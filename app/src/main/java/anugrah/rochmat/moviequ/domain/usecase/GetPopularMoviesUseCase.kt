package anugrah.rochmat.moviequ.domain.usecase

import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Observable

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    fun invoke(page: Int = 1): Observable<MovieList> {
        return repository.getPopularMovies(page)
    }
}