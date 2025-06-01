package anugrah.rochmat.moviequ.data.datasource.remote

import anugrah.rochmat.moviequ.BuildConfig
import anugrah.rochmat.moviequ.data.model.GenreResponse
import anugrah.rochmat.moviequ.data.model.MovieResponse
import io.reactivex.rxjava3.core.Observable

interface MovieRemoteDataSource {
    fun getPopularMovies(page: Int = 1): Observable<MovieResponse>
    fun getMovieGenre(): Observable<GenreResponse>
}

class MovieRemoteDataSourceImpl(
    private val apiService: TMDBApiService
): MovieRemoteDataSource {

    override fun getPopularMovies(page: Int): Observable<MovieResponse> {
        return apiService.getPopularMovies(
            apiKey = BuildConfig.TMDB_API_KEY,
            page = page
        )
    }

    override fun getMovieGenre(): Observable<GenreResponse> {
        return apiService.getMovieGenres(BuildConfig.TMDB_API_KEY)
    }
}