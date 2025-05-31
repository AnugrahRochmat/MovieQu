package anugrah.rochmat.moviequ.util

object UiConstants {
    private const val TMDB_IMAGE_W500_URL = "https://image.tmdb.org/t/p/w500"
    private const val TMDB_IMAGE_ORIGINAL_URL = "https://image.tmdb.org/t/p/original/"

    fun getMovieSmallPosterUrl(posterPath: String): String {
        return "$TMDB_IMAGE_W500_URL$posterPath"
    }

    fun getMovieOriginalPosterUrl(posterPath: String): String {
        return "$TMDB_IMAGE_ORIGINAL_URL$posterPath"
    }
}