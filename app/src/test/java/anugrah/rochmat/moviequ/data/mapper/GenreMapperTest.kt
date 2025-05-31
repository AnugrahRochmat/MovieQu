package anugrah.rochmat.moviequ.data.mapper

import anugrah.rochmat.moviequ.data.datasource.local.MovieGenreEntity
import anugrah.rochmat.moviequ.data.model.GenreData
import anugrah.rochmat.moviequ.domain.entity.Genre
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GenreMapperTest {

    @Test
    fun `mapToMovieGenreEntity maps was correctly with valid data`() {
        //when
        val result = GenreMapper.mapToMovieGenreEntity(mockGenre)

        //then
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).contains("Action")
    }

    @Test
    fun `mapToDomain maps was correctly with valid data`() {
        //when
        val result = GenreMapper.mapToDomain(mockMovieGenreEntity)

        //then
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).contains("Action")
    }

    @Test
    fun `mapToMovieGenreEntityList maps was correctly with valid data`() {
        //when
        val result = GenreMapper.mapToMovieGenreEntityList(listOf(mockGenre))

        //then
        assertThat(result[0].id).isEqualTo(1)
        assertThat(result[0].name).contains("Action")
    }

    @Test
    fun `mapToDomainList maps was correctly with valid data`() {
        //when
        val result = GenreMapper.mapToDomainList(listOf(mockMovieGenreEntity))

        //then
        assertThat(result[0].id).isEqualTo(1)
        assertThat(result[0].name).contains("Action")
    }

    @Test
    fun `mapGenreDatasToDomain maps was correctly with valid data`() {
        //when
        val result = GenreMapper.mapGenreDatasToDomain(mockGenreData)

        //then
        assertThat(result[1].id).isEqualTo(2)
        assertThat(result[1].name).contains("Comedy")
    }

    private val mockGenre = Genre(
        id = 1,
        name = "Action"
    )

    private val mockMovieGenreEntity = MovieGenreEntity(
        id = 1,
        name = "Action"
    )

    private val mockGenreData = listOf(
        GenreData(1, "Action"),
        GenreData(2, "Comedy")
    )
}