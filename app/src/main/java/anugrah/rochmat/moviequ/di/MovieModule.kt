package anugrah.rochmat.moviequ.di

import anugrah.rochmat.moviequ.data.datasource.local.MovieLocalDataSource
import anugrah.rochmat.moviequ.data.datasource.local.MovieLocalDataSourceImpl
import anugrah.rochmat.moviequ.data.datasource.remote.MovieRemoteDataSource
import anugrah.rochmat.moviequ.data.datasource.remote.MovieRemoteDataSourceImpl
import anugrah.rochmat.moviequ.data.repository.MovieRepositoryImpl
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import anugrah.rochmat.moviequ.domain.usecase.GetPopularMoviesUseCase
import anugrah.rochmat.moviequ.presentation.viewmodel.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


private val movieDataSourceModule = module {
    single<MovieRemoteDataSource> { MovieRemoteDataSourceImpl(get()) }
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get(), get()) }
}

private val movieBusinessModule = module {
    single { GetPopularMoviesUseCase(get()) }
    viewModel { MovieViewModel(get()) }
}

val movieModules = listOf(
    movieDataSourceModule,
    movieBusinessModule
)