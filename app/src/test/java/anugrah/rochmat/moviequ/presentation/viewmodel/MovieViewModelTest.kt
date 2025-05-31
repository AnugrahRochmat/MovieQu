package anugrah.rochmat.moviequ.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.domain.usecase.GetPopularMoviesUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MovieViewModelTest {
    @MockK
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase

    private lateinit var viewModel: MovieViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        // Mock RxJava schedulers for testing
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun tearDown() {
        // Clean up RxJava schedulers after each test
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun `loadPopularMovies updates UI state to loading then success`() {
        // Given
        val movieList = MovieList(1, emptyList())
        every { getPopularMoviesUseCase() } returns Observable.just(movieList)

        // When - Create ViewModel AFTER mocking (so init() uses the mock)
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState?.isLoading).isFalse()
        assertThat(uiState?.popularMovies).isEqualTo(movieList)
        assertThat(uiState?.error).isNull()

        // More specific assertions
        assertThat(uiState?.popularMovies?.page).isEqualTo(1)
        assertThat(uiState?.popularMovies?.movies).isEmpty()
    }

    @Test
    fun `loadPopularMovies handles error correctly`() {
        // Given
        val errorMessage = "Network error"
        every { getPopularMoviesUseCase() } returns Observable.error(Exception(errorMessage))

        // When - Create ViewModel AFTER mocking
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState?.isLoading).isFalse()
        assertThat(uiState?.error).isEqualTo(errorMessage)
        assertThat(uiState?.popularMovies).isNull()
    }

    @Test
    fun `clearError sets error to null`() {
        // Given - Start with an error state
        every { getPopularMoviesUseCase() } returns Observable.error(Exception("Test error"))
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Verify error exists
        assertThat(viewModel.uiState.value?.error).isNotNull()

        // When
        viewModel.clearError()

        // Then
        assertThat(viewModel.uiState.value?.error).isNull()
    }

    @Test
    fun `loadPopularMovies called manually after init works correctly`() {
        // Given - Mock the use case
        val movieList = MovieList(2, emptyList())
        every { getPopularMoviesUseCase() } returns Observable.just(movieList)

        // Create ViewModel with different mock first
        every { getPopularMoviesUseCase() } returns Observable.just(MovieList(1, emptyList()))
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // When - Call manually with new mock
        every { getPopularMoviesUseCase() } returns Observable.just(movieList)
        viewModel.loadPopularMovies()

        // Then
        val uiState = viewModel.uiState.value
        assertThat(uiState?.isLoading).isFalse()
        assertThat(uiState?.popularMovies?.page).isEqualTo(2)
        assertThat(uiState?.error).isNull()
    }

    @Test
    fun `initial state is loading`() {
        // Given - Mock to return an observable that doesn't complete immediately
        val testObserver = PublishSubject.create<MovieList>()
        every { getPopularMoviesUseCase() } returns testObserver

        // When
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Then - Should be in loading state initially
        val uiState = viewModel.uiState.value
        assertThat(uiState?.isLoading).isTrue()
        assertThat(uiState?.popularMovies).isNull()
        assertThat(uiState?.error).isNull()
    }

    @Test
    fun `compositeDisposable disposes subscriptions when ViewModel is cleared`() {
        // Given - Create observable that we can control
        val testSubject = PublishSubject.create<MovieList>()
        every { getPopularMoviesUseCase() } returns testSubject

        // Create ViewModel (this subscribes to the observable)
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Verify subscription is active by checking observer count
        assertThat(testSubject.hasObservers()).isTrue()

        // When - Use reflection to call protected onCleared() method
        val onClearedMethod = ViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)

        // Then - Verify the subscription has been disposed
        assertThat(testSubject.hasObservers()).isFalse()
    }

    @Test
    fun `multiple subscriptions are disposed when ViewModel is cleared`() {
        // Given - Create multiple observables
        val testSubject1 = PublishSubject.create<MovieList>()
        val testSubject2 = PublishSubject.create<MovieList>()

        // First call (init)
        every { getPopularMoviesUseCase() } returns testSubject1
        viewModel = MovieViewModel(getPopularMoviesUseCase)

        // Second manual call
        every { getPopularMoviesUseCase() } returns testSubject2
        viewModel.loadPopularMovies()

        // Verify both subscriptions are active
        assertThat(testSubject1.hasObservers()).isTrue()
        assertThat(testSubject2.hasObservers()).isTrue()

        // When - Clear ViewModel using reflection
        val onClearedMethod = ViewModel::class.java.getDeclaredMethod("onCleared")
        onClearedMethod.isAccessible = true
        onClearedMethod.invoke(viewModel)

        // Then - Both subscriptions should be disposed
        assertThat(testSubject1.hasObservers()).isFalse()
        assertThat(testSubject2.hasObservers()).isFalse()
    }
}