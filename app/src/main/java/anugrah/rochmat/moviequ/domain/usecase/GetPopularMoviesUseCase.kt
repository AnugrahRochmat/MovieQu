package anugrah.rochmat.moviequ.domain.usecase

import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Observable

class GetPopularMoviesUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Observable<MovieList> {
        return repository.getPopularMovies()
    }
}