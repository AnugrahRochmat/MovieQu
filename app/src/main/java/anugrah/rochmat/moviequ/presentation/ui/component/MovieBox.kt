package anugrah.rochmat.moviequ.presentation.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import anugrah.rochmat.moviequ.domain.entity.Movie
import anugrah.rochmat.moviequ.util.MockDataPreview
import anugrah.rochmat.moviequ.util.UiConstants
import anugrah.rochmat.moviequ.R
import anugrah.rochmat.moviequ.util.DateUtil
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun MovieBox(movie: Movie) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // First Column - Poster
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                MoviePoster(
                    posterPath = movie.posterPath,
                    contentDescription = stringResource(R.string.movie_poster)
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // Second Column - Poster
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Title
                Text(
                    text = movie.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Year
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.movie_year),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = DateUtil.getYearFromReleaseDate(movie.releaseDate),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Genre
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        imageVector = Icons.Default.Theaters,
                        contentDescription = stringResource(R.string.movie_year),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = movie.genre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Overview
                Text(
                    text = movie.overview,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun MoviePoster(
    posterPath: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val imageUrl = UiConstants.getMovieSmallPosterUrl(posterPath)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp)),
        contentScale = ContentScale.Fit,
        placeholder = painterResource(R.drawable.img_poster_placeholder),
        error = painterResource(R.drawable.img_poster_placeholder)
    )
}

// ============ PREVIEWS ============
@Preview(
    name = "Movie Preview - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun MovieCardDarkPreview() {
    MaterialTheme {
        MovieBox(MockDataPreview.mockMovie)
    }
}