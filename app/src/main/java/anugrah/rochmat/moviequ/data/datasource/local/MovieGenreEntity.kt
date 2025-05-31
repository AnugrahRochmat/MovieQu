package anugrah.rochmat.moviequ.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_genres")
data class MovieGenreEntity(
    @PrimaryKey val id: Int,
    val name: String
)