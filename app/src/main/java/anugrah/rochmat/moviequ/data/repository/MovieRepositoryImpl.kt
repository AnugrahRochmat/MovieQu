package anugrah.rochmat.moviequ.data.repository

import anugrah.rochmat.moviequ.data.datasource.local.MovieLocalDataSource
import anugrah.rochmat.moviequ.data.datasource.remote.MovieRemoteDataSource
import anugrah.rochmat.moviequ.data.mapper.GenreMapper
import anugrah.rochmat.moviequ.data.mapper.MovieMapper
import anugrah.rochmat.moviequ.domain.entity.Genre
import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MovieRepositoryImpl(
    private val remoteDataSource: MovieRemoteDataSource,
    private val localDataSource: MovieLocalDataSource
): MovieRepository {

    override fun getPopularMovies(): Observable<MovieList> {
        return Observable.zip(
            remoteDataSource.getPopularMovies(),
            getMovieGenresFromCacheOrNetwork()
        ) { movieResponse, movieGenres ->
            MovieMapper.mapMovieResponseToDomain(movieResponse, movieGenres)
        }.subscribeOn(Schedulers.io())
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