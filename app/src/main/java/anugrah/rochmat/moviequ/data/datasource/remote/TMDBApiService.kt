package anugrah.rochmat.moviequ.data.datasource.remote

import anugrah.rochmat.moviequ.data.model.GenreResponse
import anugrah.rochmat.moviequ.data.model.MovieResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApiService {

    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-us",
        @Query("page") page: String = "1"
    ): Observable<MovieResponse>

    @GET("genre/movie/list")
    fun getMovieGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-us"
    ): Observable<GenreResponse>
}