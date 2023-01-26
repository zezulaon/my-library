package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.zezula.books.R
import dev.zezula.books.data.model.review.Review

@Composable
fun TabReviews(
    uiState: BookDetailUiState,
    onReviewClick: (review: Review) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (uiState.isInProgress) {
            CircularProgressIndicator(
                modifier = modifier
                    .padding(top = 32.dp)
                    .align(Alignment.Center)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    RatingCard(
                        modifier = modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        uiState = uiState
                    )
                }
                itemsIndexed(items = uiState.reviews, key = { _, item -> item.id }) { _, review ->
                    ReviewCard(
                        modifier = modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                        review = review,
                        onReviewClick = onReviewClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingCard(
    uiState: BookDetailUiState,
    modifier: Modifier = Modifier,
) {
    val rating = uiState.rating
    if (rating?.averageRating != null) {
        ElevatedCard(
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = rating.averageRating, style = MaterialTheme.typography.headlineMedium)
                }
                val ratingReviewLabel = stringResource(
                    id = R.string.detail_label_rating_reviews_count,
                    rating.ratingsCountFormatted,
                    rating.textReviewsCountFormatted
                )
                Text(text = ratingReviewLabel)
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: Review,
    onReviewClick: (review: Review) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserImage(modifier = Modifier.size(64.dp), review = review)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = review.userName ?: "", style = MaterialTheme.typography.titleMedium)
                    RatingLabel(review)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = review.body ?: "", maxLines = 10, overflow = TextOverflow.Ellipsis)
            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth(), thickness = .5.dp
            )
            FilledTonalButton(modifier = Modifier
                .padding(top = 0.dp)
                .align(End), onClick = { onReviewClick(review) }) {
                Text(text = stringResource(R.string.detail_reviews_btn_read_more))
            }
        }
    }
}

@Composable
private fun RatingLabel(
    review: Review,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(R.string.detail_label_rated))
        Row(modifier = Modifier.padding(horizontal = 4.dp)) {
            val rating = review.rating ?: 0
            (1..5).forEach { index ->
                val color = if (index <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
private fun UserImage(
    review: Review,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        GlideImage(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Crop,
            model = review.userImageLink,
            contentDescription = null,
        )
    }
}
