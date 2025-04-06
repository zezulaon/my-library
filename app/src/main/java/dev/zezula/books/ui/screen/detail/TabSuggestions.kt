package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.ui.screen.components.BookList
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun TabSuggestions(
    uiState: SuggestionsUiState,
    onBookClick: (bookId: Book.Id) -> Unit,
    modifier: Modifier = Modifier,
) {
    val suggestions = uiState.suggestions

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
                CircularProgressIndicator()
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
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewAboutCard() {
    MyLibraryTheme {
        TabSuggestions(
            uiState = SuggestionsUiState(
                suggestions = previewBooks,
                isGeneratingInProgress = false,
                refreshFailed = false,
            ),
            onBookClick = {},
        )
    }
}
