package anugrah.rochmat.moviequ.presentation.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import anugrah.rochmat.moviequ.presentation.state.MovieUiState
import anugrah.rochmat.moviequ.presentation.viewmodel.MovieViewModel
import anugrah.rochmat.moviequ.R
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.domain.entity.MovieList
import anugrah.rochmat.moviequ.presentation.ui.component.ErrorState
import anugrah.rochmat.moviequ.presentation.ui.component.MovieBox
import anugrah.rochmat.moviequ.presentation.ui.theme.MovieQuTheme
import anugrah.rochmat.moviequ.util.MockDataPreview
import kotlinx.coroutines.flow.Flow
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
            if (uiState?.usePaging3 == true) {
                viewModel.loadMoviesWithPaging3()
            } else {
                viewModel.loadPopularMovies()
            }
        },
        onRefresh = {
            if (uiState?.usePaging3 == true) {
                viewModel.loadMoviesWithPaging3()  // Refresh Paging 3
            } else {
                viewModel.loadPopularMovies(isRefresh = true)  // Refresh manual
            }
        },
        onLoadMore = {
            viewModel.loadMoreMovies()  // Only for manual pagination
        },
        onSwitchToPaging3 = {
            viewModel.switchToPaging3()
        },
        onSwitchToManual = {
            viewModel.switchToManualPagination()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MovieScreenContent(
    uiState: MovieUiState?,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onSwitchToPaging3: () -> Unit,
    onSwitchToManual: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState?.isRefreshing == true,
        onRefresh = onRefresh
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        CenterAlignedTopAppBar(
            title = { Text(text = stringResource(R.string.app_name)) }
        )

        // 🆕 Pagination mode selector
        PaginationModeSelector(
            usePaging3 = uiState?.usePaging3 == true,
            onSwitchToPaging3 = onSwitchToPaging3,
            onSwitchToManual = onSwitchToManual
        )

        // Content with pull-to-refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            when {
                // Initial loading state
                uiState?.isLoading == true &&
                        uiState.popularMovies == null &&
                        uiState.pagingDataFlow == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                // Error state with no data
                uiState?.error != null &&
                        uiState.popularMovies == null &&
                        uiState.pagingDataFlow == null -> {
                    ErrorState(
                        error = uiState.error,
                        onRetry = onRetry
                    )
                }

                // 🆕 Paging 3 content
                uiState?.usePaging3 == true && uiState.pagingDataFlow != null -> {
                    Paging3Content(
                        pagingDataFlow = uiState.pagingDataFlow
                    )
                }

                // ✅ Manual pagination content (your existing)
                uiState?.popularMovies != null -> {
                    ManualPaginationContent(
                        movieList = uiState.popularMovies,
                        isLoadingMore = uiState.isLoadingMore,
                        hasMorePages = uiState.hasMorePages,
                        onLoadMore = onLoadMore,
                        error = uiState.error,
                        onRetry = onRetry
                    )
                }
            }

            // Pull refresh indicator (works for both modes)
            PullRefreshIndicator(
                refreshing = uiState?.isRefreshing == true,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun PaginationModeSelector(
    usePaging3: Boolean,
    onSwitchToPaging3: () -> Unit,
    onSwitchToManual: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onSwitchToManual,
            modifier = Modifier.weight(1f),
            enabled = usePaging3  // Only enabled when currently using Paging 3
        ) {
            Text("Manual + Pull-to-Refresh")
        }

        Button(
            onClick = onSwitchToPaging3,
            modifier = Modifier.weight(1f),
            enabled = !usePaging3  // Only enabled when currently using Manual
        ) {
            Text("Paging 3 Auto")
        }
    }
}

@Composable
private fun Paging3Content(
    pagingDataFlow: Flow<PagingData<Movie>>?
) {
    if (pagingDataFlow == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val lazyPagingItems: LazyPagingItems<Movie> = pagingDataFlow.collectAsLazyPagingItems()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ✅ USE THIS APPROACH: itemCount with indexing
        items(
            count = lazyPagingItems.itemCount,
            key = { index ->
                val movie = lazyPagingItems.peek(index)
                if (movie != null) {
                    "${movie.movieId}_$index"  // ← Combine ID + index for uniqueness
                } else {
                    "loading_$index"  // ← Fallback for loading items
                }
            }
        ) { index ->
            val movie = lazyPagingItems[index]
            movie?.let {
                MovieBox(movie = it)
            }
        }

        // Handle Paging 3 load states
        when {
            lazyPagingItems.loadState.refresh is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading movies...",
                            modifier = Modifier.padding(top = 48.dp)
                        )
                    }
                }
            }

            lazyPagingItems.loadState.append is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Loading more...",
                            modifier = Modifier.padding(start = 48.dp)
                        )
                    }
                }
            }

            lazyPagingItems.loadState.refresh is LoadState.Error -> {
                item {
                    val e = lazyPagingItems.loadState.refresh as LoadState.Error
                    ErrorState(
                        error = e.error.localizedMessage ?: "Unknown error",
                        onRetry = { lazyPagingItems.retry() }
                    )
                }
            }

            lazyPagingItems.loadState.append is LoadState.Error -> {
                item {
                    val e = lazyPagingItems.loadState.append as LoadState.Error
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Failed to load more: ${e.error.localizedMessage ?: "Unknown error"}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { lazyPagingItems.retry() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            lazyPagingItems.loadState.refresh is LoadState.NotLoading &&
                    lazyPagingItems.itemCount == 0 -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No movies found")
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualPaginationContent(
    movieList: MovieList,
    isLoadingMore: Boolean,
    hasMorePages: Boolean,
    onLoadMore: () -> Unit,
    error: String?,
    onRetry: () -> Unit
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = movieList.movies) { movie ->
            MovieBox(movie)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        if (error != null && movieList.movies.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.retry))
                    }
                }
            }
        }
    }

    // 🔧 Fixed the load more detection
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null &&
                    lastVisibleIndex >= movieList.movies.size - 3 &&
                    hasMorePages &&
                    !isLoadingMore) {
                    onLoadMore()  // 🔧 Added parentheses to actually call the function
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
            onRetry = {},
            onRefresh = {},
            onLoadMore = {},
            onSwitchToPaging3 = {},
            onSwitchToManual = {}
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
            onRetry = {},
            onRefresh = {},
            onLoadMore = {},
            onSwitchToPaging3 = {},
            onSwitchToManual = {}
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
            onRetry = {},
            onRefresh = {},
            onLoadMore = {},
            onSwitchToPaging3 = {},
            onSwitchToManual = {}
        )
    }
}