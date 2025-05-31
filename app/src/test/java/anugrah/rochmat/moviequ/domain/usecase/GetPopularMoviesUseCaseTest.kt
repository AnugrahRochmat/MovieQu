package anugrah.rochmat.moviequ.domain.usecase

import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.Before
import org.junit.Test

class GetPopularMoviesUseCaseTest {
    @MockK
    private lateinit var repository: MovieRepository

    private lateinit var useCase: GetPopularMoviesUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetPopularMoviesUseCase(repository)
    }

    @Test
    fun `invoke calls repository getPopularMovies`() {
        //given
        val movieList = MovieList(1, emptyList())
        every { repository.getPopularMovies() } returns Observable.just(movieList)

        //when
        val result = useCase.invoke().test()

        //then
        result.assertComplete()
        result.assertValue(movieList)
        verify { repository.getPopularMovies() }
    }
}