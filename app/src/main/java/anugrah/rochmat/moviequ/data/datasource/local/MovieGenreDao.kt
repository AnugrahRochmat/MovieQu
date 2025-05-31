package anugrah.rochmat.moviequ.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Observable

@Dao
interface MovieGenreDao {
    @Query("SELECT * FROM movie_genres")
    fun getAllGenres(): Observable<List<MovieGenreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGenres(genres: List<MovieGenreEntity>)

    @Query("DELETE FROM movie_genres")
    fun clearGenres()
}