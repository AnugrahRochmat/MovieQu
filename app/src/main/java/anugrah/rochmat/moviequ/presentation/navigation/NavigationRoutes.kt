package anugrah.rochmat.moviequ.presentation.navigation

sealed class NavigationRoutes(val route: String) {
    data object PopularMovies : NavigationRoutes("popular_movies")
}