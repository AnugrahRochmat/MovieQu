package anugrah.rochmat.moviequ.data.datasource.local

import anugrah.rochmat.moviequ.data.mapper.GenreMapper
import anugrah.rochmat.moviequ.domain.entity.Genre
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

interface MovieLocalDataSource {
    fun getMovieGenres(): Observable<List<Genre>>
    fun saveMovieGenres(genres: List<Genre>): Completable
}

class MovieLocalDataSourceImpl(
    private val movieGenreDao: MovieGenreDao
) : MovieLocalDataSource {

    override fun getMovieGenres(): Observable<List<Genre>> {
        return movieGenreDao.getAllGenres()
            .subscribeOn(Schedulers.io())
            .map { GenreMapper.mapToDomainList(it) }
    }

    override fun saveMovieGenres(genres: List<Genre>): Completable {
        return Completable.fromCallable {
            val entities = GenreMapper.mapToMovieGenreEntityList(genres)
            movieGenreDao.insertGenres(entities)
        }.subscribeOn(Schedulers.io())
    }
}