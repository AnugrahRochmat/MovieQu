package anugrah.rochmat.moviequ.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateUtil {
    fun getYearFromReleaseDate(releaseDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return try {
            val date = LocalDate.parse(releaseDate, formatter)
            date.year.toString()
        } catch (e: DateTimeParseException) {
            ""
        }
    }
}