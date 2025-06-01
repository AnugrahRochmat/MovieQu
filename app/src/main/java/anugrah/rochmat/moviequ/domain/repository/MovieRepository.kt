package anugrah.rochmat.moviequ.domain.repository

import androidx.paging.PagingData
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getPopularMovies(page: Int = 1): Observable<MovieList>
    fun getPopularMoviesPaging(): Observable<Flow<PagingData<Movie>>>
}