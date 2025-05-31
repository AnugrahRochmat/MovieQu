package anugrah.rochmat.moviequ.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import anugrah.rochmat.moviequ.data.datasource.local.MovieLocalDataSource
import anugrah.rochmat.moviequ.data.datasource.remote.MovieRemoteDataSource
import anugrah.rochmat.moviequ.data.model.GenreData
import anugrah.rochmat.moviequ.data.model.GenreResponse
import anugrah.rochmat.moviequ.data.model.MovieData
import anugrah.rochmat.moviequ.data.model.MovieResponse
import anugrah.rochmat.moviequ.domain.entity.Genre
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieRepositoryImplTest {

    @MockK
    private lateinit var remoteDataSource: MovieRemoteDataSource

    @MockK
    private lateinit var localDataSource: MovieLocalDataSource

    private lateinit var repository: MovieRepositoryImpl

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        // Mock RxJava schedulers for testing
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }

        repository = MovieRepositoryImpl(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
    }

    // =============== SUCCESS SCENARIOS ===============

    @Test
    fun `getPopularMovies returns mapped data when cached genres available`() {
        // Given
        val movieData = createTestMovieData(id = 1, title = "Test Movie", genreIds = listOf(1))
        val movieResponse = MovieResponse(page = 1, listMovie = listOf(movieData))
        val cachedGenres = listOf(Genre(id = 1, name = "Action"))

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(cachedGenres)

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertComplete()
        result.assertNoErrors()
        result.assertValueCount(1)

        val movieList = result.values()[0]
        assertThat(movieList.page).isEqualTo(1)
        assertThat(movieList.movies).hasSize(1)
        assertThat(movieList.movies[0].title).isEqualTo("Test Movie")
        assertThat(movieList.movies[0].genre).isEqualTo("Action")

        verify { remoteDataSource.getPopularMovies() }
        verify { localDataSource.getMovieGenres() }
        verify(exactly = 0) { remoteDataSource.getMovieGenre() }
        verify(exactly = 0) { localDataSource.saveMovieGenres(any()) }
    }

    @Test
    fun `getPopularMovies fetches and caches genres when local cache is empty`() {
        // Given
        val movieData = createTestMovieData(id = 1, title = "Test Movie", genreIds = listOf(2))
        val movieResponse = MovieResponse(page = 1, listMovie = listOf(movieData))
        val emptyLocalGenres = emptyList<Genre>()
        val remoteGenresData = listOf(GenreData(id = 2, name = "Comedy"))
        val genreResponse = GenreResponse(genres = remoteGenresData)

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(emptyLocalGenres)
        every { remoteDataSource.getMovieGenre() } returns Observable.just(genreResponse)
        every { localDataSource.saveMovieGenres(any()) } returns Completable.complete()

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertComplete()
        result.assertNoErrors()
        result.assertValueCount(1)

        val movieList = result.values()[0]
        assertThat(movieList.movies[0].genre).isEqualTo("Comedy")

        verify { remoteDataSource.getPopularMovies() }
        verify { localDataSource.getMovieGenres() }
        verify { remoteDataSource.getMovieGenre() }
        verify { localDataSource.saveMovieGenres(match { genres ->
            genres.size == 1 && genres[0].name == "Comedy"
        }) }
    }

    @Test
    fun `getPopularMovies with multiple movies and genres maps correctly`() {
        // Given
        val movieData1 = createTestMovieData(id = 1, title = "Action Movie", genreIds = listOf(1))
        val movieData2 = createTestMovieData(id = 2, title = "Comedy Movie", genreIds = listOf(2))
        val movieResponse = MovieResponse(page = 2, listMovie = listOf(movieData1, movieData2))
        val genres = listOf(
            Genre(id = 1, name = "Action"),
            Genre(id = 2, name = "Comedy")
        )

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(genres)

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertComplete()

        val movieList = result.values()[0]
        assertThat(movieList.page).isEqualTo(2)
        assertThat(movieList.movies).hasSize(2)
        assertThat(movieList.movies[0].genre).isEqualTo("Action")
        assertThat(movieList.movies[1].genre).isEqualTo("Comedy")
    }

    // =============== ERROR SCENARIOS ===============

    @Test
    fun `getPopularMovies handles remote data source error`() {
        // Given
        val errorMessage = "Network error"
        val genres = listOf(Genre(id = 1, name = "Action"))

        every { remoteDataSource.getPopularMovies() } returns Observable.error(Exception(errorMessage))
        every { localDataSource.getMovieGenres() } returns Observable.just(genres)

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertNotComplete()
        result.assertError(Exception::class.java)
        result.assertError { throwable ->
            throwable.message == errorMessage
        }

        verify { remoteDataSource.getPopularMovies() }
        verify { localDataSource.getMovieGenres() }
    }

    @Test
    fun `getPopularMovies handles local data source error when cache is empty`() {
        // Given
        val movieResponse = MovieResponse(page = 1, listMovie = emptyList())
        val errorMessage = "Database error"

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.error(Exception(errorMessage))

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertNotComplete()
        result.assertError(Exception::class.java)
        result.assertError { throwable ->
            throwable.message == errorMessage
        }
    }

    @Test
    fun `getPopularMovies handles remote genre fetch error when cache is empty`() {
        // Given
        val movieResponse = MovieResponse(page = 1, listMovie = emptyList())
        val emptyGenres = emptyList<Genre>()
        val errorMessage = "Genre fetch failed"

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(emptyGenres)
        every { remoteDataSource.getMovieGenre() } returns Observable.error(Exception(errorMessage))

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertNotComplete()
        result.assertError(Exception::class.java)
        result.assertError { throwable ->
            throwable.message == errorMessage
        }

        verify { remoteDataSource.getPopularMovies() }
        verify { localDataSource.getMovieGenres() }
        verify { remoteDataSource.getMovieGenre() }
        verify(exactly = 0) { localDataSource.saveMovieGenres(any()) }
    }

    @Test
    fun `getPopularMovies handles genre save error but still returns data`() {
        // Given
        val movieData = createTestMovieData(id = 1, title = "Test Movie", genreIds = listOf(1))
        val movieResponse = MovieResponse(page = 1, listMovie = listOf(movieData))
        val emptyLocalGenres = emptyList<Genre>()
        val remoteGenresData = listOf(GenreData(id = 1, name = "Action"))
        val genreResponse = GenreResponse(genres = remoteGenresData)
        val saveError = Exception("Save failed")

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(emptyLocalGenres)
        every { remoteDataSource.getMovieGenre() } returns Observable.just(genreResponse)
        every { localDataSource.saveMovieGenres(any()) } returns Completable.error(saveError)

        // When
        val result = repository.getPopularMovies().test()

        // Then - Should still complete successfully despite save error
        result.assertComplete()
        result.assertNoErrors()

        val movieList = result.values()[0]
        assertThat(movieList.movies[0].genre).isEqualTo("Action")

        verify { localDataSource.saveMovieGenres(any()) }
    }

    // =============== EDGE CASES ===============

    @Test
    fun `getPopularMovies handles empty movie list`() {
        // Given
        val movieResponse = MovieResponse(page = 1, listMovie = emptyList())
        val genres = listOf(Genre(id = 1, name = "Action"))

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(genres)

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertComplete()

        val movieList = result.values()[0]
        assertThat(movieList.page).isEqualTo(1)
        assertThat(movieList.movies).isEmpty()
    }

    @Test
    fun `getPopularMovies handles movie with unknown genre`() {
        // Given
        val movieData = createTestMovieData(id = 1, title = "Mystery Movie", genreIds = listOf(999))
        val movieResponse = MovieResponse(page = 1, listMovie = listOf(movieData))
        val genres = listOf(Genre(id = 1, name = "Action")) // Genre 999 not in list

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns Observable.just(genres)

        // When
        val result = repository.getPopularMovies().test()

        // Then
        result.assertComplete()

        val movieList = result.values()[0]
        assertThat(movieList.movies[0].genre).isEmpty() // Should be empty string for unknown genre
    }

    @Test
    fun `getMovieGenresFromCacheOrNetwork takes only first emission from cache`() {
        // Given
        val movieResponse = MovieResponse(page = 1, listMovie = emptyList())
        val firstGenres = listOf(Genre(id = 1, name = "Action"))
        val secondGenres = listOf(Genre(id = 2, name = "Comedy"))

        // Create a subject that emits multiple values
        val genreSubject = PublishSubject.create<List<Genre>>()

        every { remoteDataSource.getPopularMovies() } returns Observable.just(movieResponse)
        every { localDataSource.getMovieGenres() } returns genreSubject

        // When
        val result = repository.getPopularMovies().test()

        // Emit first value
        genreSubject.onNext(firstGenres)
        // Emit second value (should be ignored due to take(1))
        genreSubject.onNext(secondGenres)

        // Then
        result.assertComplete()
        result.assertValueCount(1) // Should only receive one emission

        verify(exactly = 1) { localDataSource.getMovieGenres() }
    }

    // =============== HELPER METHODS ===============

    private fun createTestMovieData(
        id: Int = 1,
        title: String = "Test Movie",
        genreIds: List<Int> = listOf(1),
        overview: String = "Test overview",
        posterPath: String = "/test.jpg",
        releaseDate: String = "2023-05-15"
    ) = MovieData(
        genreIds = genreIds,
        movieId = id,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        title = title
    )
}