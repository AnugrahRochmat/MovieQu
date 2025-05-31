package anugrah.rochmat.moviequ.presentation.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import anugrah.rochmat.moviequ.presentation.state.MovieUiState
import anugrah.rochmat.moviequ.presentation.viewmodel.MovieViewModel
import anugrah.rochmat.moviequ.R
import anugrah.rochmat.moviequ.presentation.ui.component.ErrorState
import anugrah.rochmat.moviequ.presentation.ui.component.MovieBox
import anugrah.rochmat.moviequ.presentation.ui.theme.MovieQuTheme
import anugrah.rochmat.moviequ.util.MockDataPreview
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieScreen(
    viewModel: MovieViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.observeAsState()

    MovieScreenContent(
        uiState = uiState,
        onRetry = {
            viewModel.clearError()
            viewModel.loadPopularMovies()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreenContent(
    uiState: MovieUiState?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Center Top App Bar
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) },
        )

        when {
            uiState?.isLoading == true -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState?.error != null -> {
                ErrorState(
                    error = uiState.error,
                    onRetry = onRetry
                )
            }

            uiState?.popularMovies != null -> {
                Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(items = uiState.popularMovies.movies) { movie ->
                                MovieBox(movie = movie)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============ PREVIEWS ============
@Preview(
    name = "Movie Screen - Success Preview",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WeatherScreenSuccessPreview() {
    MovieQuTheme {
        MovieScreenContent(
            uiState = MovieUiState(popularMovies = MockDataPreview.mockMovieList),
            onRetry = {}
        )
    }
}

@Preview(
    name = "Movie Screen - Loading Preview",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WeatherScreenLoadingPreview() {
    MovieQuTheme {
        MovieScreenContent(
            uiState = MovieUiState(isLoading = true),
            onRetry = {}
        )
    }
}

@Preview(
    name = "Movie Screen - Error Preview",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun WeatherScreenErrorPreview() {
    MovieQuTheme {
        MovieScreenContent(
            uiState = MovieUiState(error = "Network connection failed"),
            onRetry = {}
        )
    }
}