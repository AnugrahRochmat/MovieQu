package anugrah.rochmat.moviequ.data.datasource.remote

import anugrah.rochmat.moviequ.BuildConfig
import anugrah.rochmat.moviequ.data.model.GenreResponse
import anugrah.rochmat.moviequ.data.model.MovieResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.Test

class MovieRemoteDataSourceImplTest {
    @MockK
    private lateinit var apiService: TMDBApiService

    private lateinit var dataSource: MovieRemoteDataSourceImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        dataSource = MovieRemoteDataSourceImpl(apiService)
    }

    @Test
    fun `getPopularMovies calls API with correct parameters`() {
        // Given
        val expectedResponse = MovieResponse(1, emptyList())
        every { apiService.getPopularMovies(any(), any(), any()) } returns Observable.just(expectedResponse)

        // When
        val result = dataSource.getPopularMovies().test()

        // Then
        result.assertComplete()
        result.assertValue(expectedResponse)
        verify { apiService.getPopularMovies(BuildConfig.TMDB_API_KEY, "en-us", "1") }
    }

    @Test
    fun `getMovieGenre calls API with correct parameters`() {
        // Given
        val expectedResponse = GenreResponse(listOf())
        every { apiService.getMovieGenres(any(), any()) } returns Observable.just(expectedResponse)

        // When
        val result = dataSource.getMovieGenre().test()

        // Then
        result.assertComplete()
        result.assertValue(expectedResponse)
        verify { apiService.getMovieGenres(BuildConfig.TMDB_API_KEY, "en-us") }
    }
}