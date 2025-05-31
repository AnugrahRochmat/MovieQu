package anugrah.rochmat.moviequ.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MovieResponse(
    @Json(name = "page") val page: Int,
    @Json(name = "results") val listMovie: List<MovieData>
)

@JsonClass(generateAdapter = true)
class MovieData(
    @Json(name = "genre_ids") val genreIds: List<Int>,
    @Json(name = "id") val movieId: Int,
    @Json(name = "overview") val overview: String,
    @Json(name = "poster_path") val posterPath: String,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "title") val title: String
)