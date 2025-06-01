package anugrah.rochmat.moviequ.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import anugrah.rochmat.moviequ.data.datasource.local.MovieLocalDataSource
import anugrah.rochmat.moviequ.data.datasource.remote.MoviePagingSource
import anugrah.rochmat.moviequ.data.datasource.remote.MovieRemoteDataSource
import anugrah.rochmat.moviequ.data.mapper.GenreMapper
import anugrah.rochmat.moviequ.data.mapper.MovieMapper
import anugrah.rochmat.moviequ.domain.entity.Genre
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asObservable

class MovieRepositoryImpl(
    private val remoteDataSource: MovieRemoteDataSource,
    private val localDataSource: MovieLocalDataSource
): MovieRepository {

    override fun getPopularMovies(page: Int): Observable<MovieList> {
        return Observable.zip(
            remoteDataSource.getPopularMovies(page),
            getMovieGenresFromCacheOrNetwork()
        ) { movieResponse, movieGenres ->
            MovieMapper.mapMovieResponseToDomain(movieResponse, movieGenres)
        }.subscribeOn(Schedulers.io())
    }

    override fun getPopularMoviesPaging(): Observable<Flow<PagingData<Movie>>> {
        return getMovieGenresFromCacheOrNetwork()
            .take(1)
            .map { genres ->
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false,
                        initialLoadSize = 20,
                        prefetchDistance = 5
                    ),
                    pagingSourceFactory = {
                        MoviePagingSource(remoteDataSource, genres)
                    }
                ).flow  // 🔄 Return the Flow directly, not Observable
            }
            .subscribeOn(Schedulers.io())
    }

    private fun getMovieGenresFromCacheOrNetwork(): Observable<List<Genre>> {
        return localDataSource.getMovieGenres()
            .take(1)
            .flatMap { cachedGenres ->
                if (cachedGenres.isNotEmpty()) {
                    Observable.just(cachedGenres)
                } else {
                    remoteDataSource.getMovieGenre()
                        .map { response ->
                            GenreMapper.mapGenreDatasToDomain(response.genres)
                        }
                        .doOnNext { genres ->
                            localDataSource.saveMovieGenres(genres)
                        }
                }
            }
    }
}