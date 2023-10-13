package dev.zezula.books.ui.screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R

@Composable
fun EditableStarRating(
    userRating: Int?,
    modifier: Modifier = Modifier,
    onRatingStarSelected: ((Int) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.detail_label_your_rating), style = MaterialTheme.typography.labelLarge)
        FiveRatingPlacement(userRating) { starId, isChecked ->
            Star(
                modifier = Modifier
                    .size(54.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clickable(enabled = onRatingStarSelected != null) { onRatingStarSelected?.invoke(starId) },
                isChecked = isChecked,
            )
        }
    }
}

@Composable
fun StarRating(
    userRating: Int?,
    modifier: Modifier = Modifier,
) {
    FiveRatingPlacement(userRating) { _, isChecked ->
        Star(modifier = modifier, isChecked = isChecked)
    }
}

@Composable
private fun FiveRatingPlacement(
    userRating: Int?,
    content: @Composable RowScope.(starNumber: Int, isChecked: Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val isChecked = if (userRating != null) i <= userRating else false
            content(i, isChecked)
        }
    }
}

@Composable
private fun Star(
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
) {
    val tint = if (isChecked) {
        MaterialTheme.colorScheme.inversePrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Icon(
        modifier = modifier,
        imageVector = Icons.Filled.Star,
        contentDescription = null,
        tint = tint,
    )
}
