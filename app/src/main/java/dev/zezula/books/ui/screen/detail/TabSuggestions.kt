package dev.zezula.books.ui.screen.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.ui.screen.components.BookList
import dev.zezula.books.ui.theme.MyLibraryTheme

private const val DURATION_TO_REFRESH_IN_MILLIS = 35000

@Composable
fun TabSuggestions(
    uiState: SuggestionsUiState,
    onBookClick: (bookId: String) -> Unit,
    onGenerateSuggestionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestions = uiState.suggestions

    val animatedProgress by animateFloatAsState(
        targetValue = uiState.progress,
        animationSpec = TweenSpec(durationMillis = DURATION_TO_REFRESH_IN_MILLIS, easing = LinearEasing),
        label = "generate_suggestions_progress",
    )

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        if (suggestions.isNotEmpty()) {
            BookList(
                books = suggestions,
                onBookClick = onBookClick,
                animateItemChanges = false,
                contentPadding = PaddingValues(bottom = 88.dp),
            )
        } else if (uiState.isGeneratingInProgress) {
            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.detail_suggestions_label_creating))
                Spacer(Modifier.requiredHeight(16.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                )
            }
        } else if (uiState.refreshFailed) {
            Text(
                modifier = modifier
                    .padding(top = 24.dp)
                    .align(Alignment.Center),
                text = stringResource(R.string.detail_suggestions_no_suggestions_were_found),
            )
        } else {
            Box(modifier.padding(24.dp)) {
                SuggestionsInfoCard(
                    onGenerateSuggestionsClick = onGenerateSuggestionsClick,
                )
            }
        }
    }
}

@Composable
fun SuggestionsInfoCard(
    onGenerateSuggestionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.detail_suggestions_info_box_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.detail_suggestions_info_box_description),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.detail_suggestions_info_box_bottom_line),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(24.dp))
            FilledTonalButton(
                modifier = Modifier.align(Alignment.End),
                onClick = onGenerateSuggestionsClick,
            ) {
                Text(text = stringResource(id = R.string.detail_suggestions_generate_btn))
            }
        }
    }
}

@Composable
@Preview
fun PreviewAboutCard() {
    MyLibraryTheme {
        SuggestionsInfoCard(onGenerateSuggestionsClick = {})
    }
}
