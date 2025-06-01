package anugrah.rochmat.moviequ.data.datasource.remote

import androidx.paging.PagingState
import androidx.paging.PagingSource
import anugrah.rochmat.moviequ.data.mapper.MovieMapper
import anugrah.rochmat.moviequ.domain.entity.Genre
import anugrah.rochmat.moviequ.domain.entity.Movie

class MoviePagingSource(
    private val remoteDataSource: MovieRemoteDataSource,
    private val genres: List<Genre>
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val page = params.key ?: 1

            // Convert RxJava Observable to suspend function
            val response = remoteDataSource.getPopularMovies(page).blockingSingle()
            val movieList = MovieMapper.mapMovieResponseToDomain(response, genres)

            LoadResult.Page(
                data = movieList.movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}