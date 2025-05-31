package anugrah.rochmat.moviequ.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MovieGenreEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieGenreDao(): MovieGenreDao
}