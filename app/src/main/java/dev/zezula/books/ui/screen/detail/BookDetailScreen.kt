package dev.zezula.books.ui.screen.detail

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.review.Review
import dev.zezula.books.data.model.shelf.ShelfForBook
import dev.zezula.books.ui.theme.MyLibraryTheme

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun BookDetailRoute(
    viewModel: BookDetailViewModel,
    onNavigateBack: () -> Unit,
    onReviewClick: (Review) -> Unit,
    onEditBookClick: (String) -> Unit,
    onBookDeletedSuccess: () -> Unit,
    onNewShelfClick: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isBookDeleted) {
        val latestOnItemDeletedSuccess by rememberUpdatedState(onBookDeletedSuccess)
        LaunchedEffect(uiState) {
            latestOnItemDeletedSuccess()
        }
    }

    uiState.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    BookDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onEditBookClick = {
            val id = checkNotNull(uiState.book?.id) { "Missing book id" }
            onEditBookClick(id)
        },
        onDeleteClick = { viewModel.deleteBook() },
        onNewShelfClick = onNewShelfClick,
        onReviewClick = onReviewClick,
        onShelfCheckedChange = { shelf, isChecked ->
            viewModel.onShelfCheckChange(shelf, isChecked)
        },
        onTabClick = {
            viewModel.onTabClick(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun BookDetailScreen(
    uiState: BookDetailUiState,
    onNavigateBack: () -> Unit,
    onEditBookClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onNewShelfClick: () -> Unit,
    onReviewClick: (Review) -> Unit,
    onShelfCheckedChange: (ShelfForBook, Boolean) -> Unit,
    onTabClick: (DetailTab) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                Column {
                    BookDetailAppBar(uiState, onNavigateBack, onNewShelfClick, onDeleteClick, onEditBookClick)
                    Column(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp)
                    ) {
                        Text(text = uiState.book?.title ?: "", style = MaterialTheme.typography.headlineSmall)
                        Text(text = uiState.book?.author ?: "")
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTab.tabIndex,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                DetailTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTab.tabIndex == index,
                        onClick = { onTabClick(tab) },
                        text = { Text(text = stringResource(tab.tabName)) }
                    )
                }
            }

            when (uiState.selectedTab) {
                DetailTab.Shelves -> TabShelves(
                    uiState = uiState,
                    onShelfCheckedChange = onShelfCheckedChange
                )
                DetailTab.Detail -> TabBookDetail(
                    uiState = uiState
                )
                DetailTab.Reviews -> TabReviews(
                    uiState = uiState,
                    onReviewClick = onReviewClick
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BookDetailAppBar(
    uiState: BookDetailUiState,
    onNavigateBack: () -> Unit,
    onNewShelfClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_navigate_back)
                )
            }
        },
        actions = {
            when (uiState.selectedTab) {
                DetailTab.Shelves -> {
                    IconButton(onClick = { onNewShelfClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.content_add_new_shelf)
                        )
                    }
                }
                DetailTab.Detail -> {
                    IconButton(onClick = { onDeleteClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.content_desc_delete)
                        )
                    }
                    IconButton(onClick = { onEditBookClick() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.content_desc_edit)
                        )
                    }
                }
                DetailTab.Reviews -> {}
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyLibraryTheme {
        BookDetailScreen(
            uiState = BookDetailUiState(
                selectedTab = DetailTab.Detail,
                shelves = listOf(ShelfForBook("1", "Test", true)),
                isBookDeleted = false,
                book = Book(
                    "id",
                    "Title",
                    "Tolkien",
                    "Desc",
                    "545454",
                    "",
                    1989,
                    565,
                    null,
                    "1999"
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onEditBookClick = {},
            onDeleteClick = {},
            onNewShelfClick = {},
            onReviewClick = {},
            onShelfCheckedChange = { _, _ -> },
            onTabClick = {}
        )
    }
}