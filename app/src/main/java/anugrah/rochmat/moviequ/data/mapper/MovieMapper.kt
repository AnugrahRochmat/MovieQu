package anugrah.rochmat.moviequ.data.mapper

import anugrah.rochmat.moviequ.data.model.MovieResponse
import anugrah.rochmat.moviequ.domain.entity.Genre
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList

object MovieMapper {
    fun mapMovieResponseToDomain(movieResponse: MovieResponse, movieGenre: List<Genre>): MovieList {
        val genres = movieGenre.associateBy({ it.id }, { it.name })
        val movies = movieResponse.listMovie
            .map { movie ->
                Movie(
                    genre = genres[movie.genreIds.first()].orEmpty(),
                    movieId = movie.movieId,
                    overview = movie.overview,
                    posterPath = movie.posterPath,
                    releaseDate = movie.releaseDate,
                    title = movie.title
                )
            }

        return MovieList(
            page = movieResponse.page,
            movies = movies,
            totalPages = movieResponse.totalPages,
            totalResult = movieResponse.totalResults
        )
    }
}