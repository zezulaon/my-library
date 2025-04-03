package dev.zezula.books.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.ui.screen.list.ImageThumbnail
import dev.zezula.books.ui.theme.MyLibraryTheme
import org.jetbrains.annotations.VisibleForTesting

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FindBookRoute(
    viewModel: FindBookViewModel,
    onNavigateBack: () -> Unit,
    onViewBookClick: (id: Book.Id) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    FindBookScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onViewBookClick = onViewBookClick,
        onSearchRequested = { query ->
            viewModel.searchBooks(query)
            keyboardController?.hide()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun FindBookScreen(
    uiState: FindBookUiState,
    onNavigateBack: () -> Unit,
    onViewBookClick: (id: Book.Id) -> Unit,
    onSearchRequested: (query: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.find_book_online_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_navigate_back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchBooksBar(onSearchRequested)
            if (uiState.isInProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.noResultsMsgDisplayed) {
                Text(text = stringResource(R.string.search_no_book_found_for_query))
            } else {
                ResultList(
                    modifier = Modifier
                        .fillMaxWidth(),
                    books = uiState.foundBooks,
                    onViewBookClick = onViewBookClick,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchBooksBar(onSearchRequested: (query: String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(bottom = 26.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        SearchBar(
            placeholder = { Text(text = stringResource(R.string.find_book_search_placeholder)) },
            query = text,
            onQueryChange = {
                text = it
            },
            onSearch = {
                onSearchRequested(it)
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = { text = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null,
                        )
                    }
                }
            },
            active = false,
            onActiveChange = {},
        ) {
        }
    }
}

@Composable
private fun ResultList(
    books: List<Book>,
    onViewBookClick: (id: Book.Id) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = books) { book ->
            SearchResultCard(
                modifier = Modifier
                    .fillMaxWidth(),
                book = book,
                onViewBookClick = onViewBookClick,
            )
        }
    }
}

@Composable
private fun SearchResultCard(
    book: Book,
    onViewBookClick: (id: Book.Id) -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier,
    ) {
        Box(modifier = Modifier.clickable { onViewBookClick(book.id) }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    ImageThumbnail(
                        modifier = Modifier
                            .width(92.dp)
                            .height(140.dp),
                        bookThumbnailUri = book.thumbnailLink,
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = book.title ?: "", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = book.author ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val isbn = "ISBN: ${book.isbn ?: "-"}"
                        Text(text = isbn, style = MaterialTheme.typography.bodyMedium)
                        val publisher = "Publisher: ${book.publisher ?: "-"}"
                        Text(
                            text = publisher,
                            style = MaterialTheme.typography.bodyMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                        )
                        val yearPublished = "Published: ${book.yearPublished ?: "-"}"
                        Text(text = yearPublished, style = MaterialTheme.typography.bodyMedium)
                        val numberOfPages = "Pages: ${book.pageCount ?: "-"}"
                        Text(text = numberOfPages, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (book.description.isNullOrEmpty().not()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = book.description ?: "", maxLines = 4, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FindBookPreview() {
    MyLibraryTheme {
        FindBookScreen(
            uiState = FindBookUiState(foundBooks = previewBooks),
            onNavigateBack = {},
            onViewBookClick = {},
            onSearchRequested = {},
        )
    }
}
