package anugrah.rochmat.moviequ.data.datasource.remote

import anugrah.rochmat.moviequ.BuildConfig
import anugrah.rochmat.moviequ.data.model.GenreResponse
import anugrah.rochmat.moviequ.data.model.MovieResponse
import io.reactivex.rxjava3.core.Observable

interface MovieRemoteDataSource {
    fun getPopularMovies(): Observable<MovieResponse>
    fun getMovieGenre(): Observable<GenreResponse>
}

class MovieRemoteDataSourceImpl(
    private val apiService: TMDBApiService
): MovieRemoteDataSource {

    override fun getPopularMovies(): Observable<MovieResponse> {
        return apiService.getPopularMovies(BuildConfig.TMDB_API_KEY)
    }

    override fun getMovieGenre(): Observable<GenreResponse> {
        return apiService.getMovieGenres(BuildConfig.TMDB_API_KEY)
    }
}