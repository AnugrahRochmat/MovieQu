package anugrah.rochmat.moviequ.domain.repository

import anugrah.rochmat.moviequ.domain.entity.MovieList
import io.reactivex.rxjava3.core.Observable

interface MovieRepository {
    fun getPopularMovies(): Observable<MovieList>
}