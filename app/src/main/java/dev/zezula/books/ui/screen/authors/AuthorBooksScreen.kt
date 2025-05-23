package dev.zezula.books.ui.screen.authors

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.ui.screen.components.BookList
import dev.zezula.books.ui.theme.MyLibraryTheme
import dev.zezula.books.util.allAuthorsAppBar

@Composable
fun AuthorBooksRoute(
    viewModel: AuthorBooksViewModel,
    onBookClick: (Book.Id) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AuthorBooksScreen(
        uiState = uiState,
        onBookClick = onBookClick,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
@VisibleForTesting
fun AuthorBooksScreen(
    uiState: AuthorBooksUiState,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onBookClick: (Book.Id) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { AuthorBooksTopAppBar(uiState = uiState, onNavigateBack = onNavigateBack) },
    ) { innerPadding ->
        BookList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            books = uiState.books,
            onBookClick = onBookClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthorBooksTopAppBar(
    uiState: AuthorBooksUiState,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.testTag(allAuthorsAppBar),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = { AllAuthorsAppBarTitle(uiState) },
        navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_navigate_back),
                )
            }
        },
    )
}

@Composable
private fun AllAuthorsAppBarTitle(uiState: AuthorBooksUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(uiState.authorName ?: "n/a")
        val numberOfBooks = uiState.books.count()
        val noAuthorsFormatted = pluralStringResource(
            R.plurals.home_number_of_books_subtitle,
            numberOfBooks,
            numberOfBooks,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(noAuthorsFormatted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
@Preview
fun PreviewAuthorBooksScreen() {
    MyLibraryTheme {
        AuthorBooksScreen(
            uiState = AuthorBooksUiState(),
            onNavigateBack = {},
        )
    }
}
