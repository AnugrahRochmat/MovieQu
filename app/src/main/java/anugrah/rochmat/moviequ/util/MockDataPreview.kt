package anugrah.rochmat.moviequ.util

import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList

object MockDataPreview {
    val mockMovie = Movie(
        genre = "Action",
        movieId = 552524,
        overview = "The wildly funny and touching story of a lonely Hawaiian girl and the fugitive alien who helps to mend her broken family.",
        posterPath = "",
        releaseDate = "2025-05-17",
        title = "Lilo & Stitch"
    )

    private val mockMovies = listOf(
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "The wildly funny and touching story of a lonely Hawaiian girl and the fugitive alien who helps to mend her broken family.",
            posterPath = "",
            releaseDate = "2025-05-17",
            title = "Lilo & Stitch"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "Plagued by a violent recurring nightmare, college student Stefanie heads home to track down the one person who might be able to break the cycle and save her family from the grisly demise that inevitably awaits them all.",
            posterPath = "",
            releaseDate = "2025-05-14",
            title = "Final Destination Bloodlines"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "One year after her sister Melanie mysteriously disappeared, Clover and her friends head into the remote valley where she vanished in search of answers. Exploring an abandoned visitor center, they find themselves stalked by a masked killer and horrifically murdered one by one...only to wake up and find themselves back at the beginning of the same evening.",
            posterPath = "",
            releaseDate = "2025-04-23",
            title = "Until Dawn"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "",
            posterPath = "",
            releaseDate = "2025-05-17",
            title = "Mission: Impossible - The Final Reckoning"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "Four misfits find themselves struggling with ordinary problems when they are suddenly pulled through a mysterious portal into the Overworld: a bizarre, cubic wonderland that thrives on imagination. To get back home, they'll have to master this world while embarking on a magical quest with an unexpected, expert crafter, Steve.",
            posterPath = "",
            releaseDate = "2025-03-31",
            title = "A Minecraft Movie"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "Levon Cade left behind a decorated military career in the black ops to live a simple life working construction. But when his boss's daughter, who is like family to him, is taken by human traffickers, his search to bring her home uncovers a world of corruption far greater than he ever could have imagined.",
            posterPath = "",
            releaseDate = "2025-03-26",
            title = "A Working Man"
        ),
        Movie(
            genre = "Action",
            movieId = 552524,
            overview = "After meeting with newly elected U.S. President Thaddeus Ross, Sam finds himself in the middle of an international incident. He must discover the reason behind a nefarious global plot before the true mastermind has the entire world seeing red.",
            posterPath = "",
            releaseDate = "2025-02-12",
            title = "Captain America: Brave New World"
        )
    )

    val mockMovieList = MovieList(
        page = 1,
        movies = mockMovies
    )
}