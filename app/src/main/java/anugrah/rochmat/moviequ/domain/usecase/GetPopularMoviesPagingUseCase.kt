package anugrah.rochmat.moviequ.domain.usecase

import androidx.paging.PagingData
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.repository.MovieRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow

class GetPopularMoviesPagingUseCase(private val repository: MovieRepository) {
    operator fun invoke(): Observable<Flow<PagingData<Movie>>> {
        return repository.getPopularMoviesPaging()
    }
}