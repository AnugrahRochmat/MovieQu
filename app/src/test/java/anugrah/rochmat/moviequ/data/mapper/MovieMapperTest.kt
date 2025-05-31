package anugrah.rochmat.moviequ.data.mapper

import anugrah.rochmat.moviequ.data.model.GenreData
import anugrah.rochmat.moviequ.data.model.MovieData
import anugrah.rochmat.moviequ.data.model.MovieResponse
import anugrah.rochmat.moviequ.domain.entity.Genre
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MovieMapperTest {

    @Test
    fun `mapMovieResponseToDomain maps was correctly with valid data`() {
        //when
        val result = MovieMapper.mapMovieResponseToDomain(mockMovieResponse, mockGenres)

        //then
        assertThat(result.page).isEqualTo(1)
        assertThat(result.movies.size).isEqualTo(2)
        assertThat(result.movies[0].movieId).isEqualTo(123)
        assertThat(result.movies[0].overview).contains("Test overview")
        assertThat(result.movies[0].posterPath).contains("/test.jpg")
        assertThat(result.movies[0].releaseDate).contains("2023-05-15")
        assertThat(result.movies[0].title).contains("Test Movie")
        assertThat(result.movies[0].genre).contains("Action")
    }

    private val mockMovieData = MovieData(
        genreIds = listOf(1, 2, 3),
        movieId = 123,
        overview = "Test overview",
        posterPath = "/test.jpg",
        releaseDate = "2023-05-15",
        title = "Test Movie"
    )

    private val mockMovieResponse = MovieResponse(1, listOf(mockMovieData, mockMovieData))

    private val mockGenres = listOf(Genre(1, "Action"), Genre(2, "Comedy"))
}