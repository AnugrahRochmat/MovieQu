package anugrah.rochmat.moviequ.domain.entity

class MovieList(
    val page: Int,
    val movies: List<Movie>
)

class Movie(
    val genre: String,
    val movieId: Int,
    val overview: String,
    val posterPath: String,
    val releaseDate: String,
    val title: String
)