package anugrah.rochmat.moviequ.domain.entity

class MovieList(
    val page: Int,
    val movies: List<Movie>,
    val totalPages: Int = 1,
    val totalResult: Int = 0
)

class Movie(
    val genre: String,
    val movieId: Int,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val title: String
)