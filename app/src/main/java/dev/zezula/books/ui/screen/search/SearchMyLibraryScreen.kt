package dev.zezula.books.ui.screen.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.ui.screen.components.BookList
import dev.zezula.books.ui.theme.MyLibraryTheme
import org.jetbrains.annotations.VisibleForTesting

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchMyLibraryRoute(
    viewModel: SearchMyLibraryViewModel,
    onNavigateBack: () -> Unit,
    onBookClick: (bookId: Book.Id) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    SearchMyLibraryScreen(
        uiState = uiState,
        searchFocusRequester = focusRequester,
        onNavigateBack = onNavigateBack,
        onBookClick = onBookClick,
        onQueryChange = { query ->
            viewModel.onSearchQueryChanged(query)
        },
        onSearchButtonClick = { keyboardController?.hide() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun SearchMyLibraryScreen(
    uiState: SearchMyLibraryUiState,
    modifier: Modifier = Modifier,
    searchFocusRequester: FocusRequester = remember { FocusRequester() },
    onNavigateBack: () -> Unit = {},
    onBookClick: (bookId: Book.Id) -> Unit = {},
    onQueryChange: (query: String) -> Unit = {},
    onSearchButtonClick: () -> Unit = {},
) {
    SearchBar(
        modifier = modifier.focusRequester(searchFocusRequester),
        placeholder = { Text(text = stringResource(R.string.search_my_library_placeholder)) },
        query = uiState.currentSearchQuery,
        onQueryChange = {
            onQueryChange(it)
        },
        onSearch = {
            onSearchButtonClick()
        },
        leadingIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_navigate_back),
                )
            }
        },
        trailingIcon = {
            if (uiState.currentSearchQuery.isEmpty().not()) {
                IconButton(onClick = {
                    onQueryChange("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                    )
                }
            }
        },
        active = true,
        onActiveChange = {},
    ) {
        BookList(books = uiState.searchResults, onBookClick = onBookClick, animateItemChanges = false)
    }
}

@Preview(showBackground = true)
@Composable
fun SearchMyLibraryPreview() {
    MyLibraryTheme {
        SearchMyLibraryScreen(
            uiState = SearchMyLibraryUiState(
                searchResults = previewBooks,
            ),
        )
    }
}
