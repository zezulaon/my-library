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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.Review
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.ShelfForBook
import dev.zezula.books.testtag.BookDetailTestTag
import dev.zezula.books.ui.theme.MyLibraryTheme
import timber.log.Timber

@Composable
fun BookDetailRoute(
    viewModel: BookDetailViewModel,
    onNavigateBack: () -> Unit,
    onReviewClick: (Review) -> Unit,
    onEditBookClick: (Book.Id) -> Unit,
    onBookDeletedSuccess: () -> Unit,
    onSuggestedBookClick: (bookId: Book.Id) -> Unit,
    onNewShelfClick: () -> Unit,
    onAmazonLinkClicked: (book: Book) -> Unit = {},
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
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

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            Timber.d("Lifecycle event: $event")
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.fetchReviews()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
        onDeleteClick = { viewModel.deleteBookRequested() },
        onNoteDeleteClick = { viewModel.deleteNote(it) },
        onNoteEditClick = { viewModel.editNoteRequested(it) },
        onDeleteConfirmClick = { viewModel.deleteBookConfirmed() },
        onDeleteDialogDismissed = { viewModel.dismissDeleteDialog() },
        onNewShelfClick = onNewShelfClick,
        onNewNoteClick = { viewModel.createNoteRequested() },
        onNoteDialogDismissed = { viewModel.dismissNoteDialog() },
        onNoteDialogSaveClick = { text -> viewModel.createNote(text) },
        onNoteDialogUpdateClick = { note, text -> viewModel.updateNote(note, text) },
        onReviewClick = onReviewClick,
        onShelfCheckedChange = { shelf, isChecked -> viewModel.onShelfCheckChange(shelf, isChecked) },
        onTabClick = { viewModel.onTabClick(it) },
        onAddBookToLibraryClick = { viewModel.addBookToLibrary() },
        onAmazonLinkClicked = onAmazonLinkClicked,
        onSuggestedBookClick = onSuggestedBookClick,
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
    onDeleteConfirmClick: () -> Unit,
    onDeleteDialogDismissed: () -> Unit,
    onNewShelfClick: () -> Unit,
    onNewNoteClick: () -> Unit,
    onNoteDialogDismissed: () -> Unit,
    onNoteDialogSaveClick: (String) -> Unit,
    onNoteDialogUpdateClick: (Note, String) -> Unit,
    onNoteEditClick: (Note) -> Unit,
    onNoteDeleteClick: (Note) -> Unit,
    onReviewClick: (Review) -> Unit,
    onShelfCheckedChange: (ShelfForBook, Boolean) -> Unit,
    onTabClick: (DetailTab) -> Unit,
    modifier: Modifier = Modifier,
    onAddBookToLibraryClick: () -> Unit = {},
    onAmazonLinkClicked: (book: Book) -> Unit = {},
    onSuggestedBookClick: (bookId: Book.Id) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    if (uiState.isDeleteDialogDisplayed) {
        DeleteDialog(onDeleteDialogDismissed, onDeleteConfirmClick)
    }
    if (uiState.isNewNoteDialogDisplayed) {
        AddOrEditNoteDialog(
            onDialogDismiss = onNoteDialogDismissed,
            onDialogSaveClick = onNoteDialogSaveClick,
            onDialogUpdateClick = onNoteDialogUpdateClick,
            selectedNote = uiState.selectedNote,
        )
    }
    Scaffold(
        modifier = modifier
            .testTag(BookDetailTestTag.ROOT),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer) {
                Column {
                    BookDetailAppBar(
                        uiState = uiState,
                        onNavigateBack = onNavigateBack,
                        onNewShelfClick = onNewShelfClick,
                        onNewNoteClick = onNewNoteClick,
                        onDeleteClick = onDeleteClick,
                        onEditBookClick = onEditBookClick,
                    )
                    Column(
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp),
                    ) {
                        Text(text = uiState.book?.title ?: "", style = MaterialTheme.typography.headlineSmall)
                        Text(text = uiState.book?.author ?: "")
                    }
                }
            }
        },
        floatingActionButton = {
            if (uiState.isBookInLibrary.not()) {
                ExtendedFloatingActionButton(
                    onClick = onAddBookToLibraryClick,
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.detail_add_to_my_library_btn_content_desc),
                        )
                    },
                    text = { Text(text = stringResource(R.string.detail_add_to_my_library_btn)) },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            // Filter tabs - if the book isn't part of user's library, some tabs are hidden.
            val tabs = DetailTab.entries.filter { uiState.isBookInLibrary || it.isVisibleOutsideLibrary }
            val currentlySelectedIndex = tabs.indexOf(uiState.selectedTab)
            PrimaryScrollableTabRow(
                modifier = Modifier.testTag(BookDetailTestTag.CONTAINER_TAB_BAR),
                selectedTabIndex = currentlySelectedIndex,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { onTabClick(tab) },
                        text = { Text(text = stringResource(tab.tabTitle)) },
                    )
                }
            }

            when (uiState.selectedTab) {
                DetailTab.Shelves -> TabShelves(
                    uiState = uiState,
                    onShelfCheckedChange = onShelfCheckedChange,
                )

                DetailTab.Detail -> TabBookDetail(
                    uiState = uiState,
                    onAmazonLinkClicked = onAmazonLinkClicked,
                )

                DetailTab.Notes -> TabNotes(
                    uiState = uiState,
                    onEditClick = onNoteEditClick,
                    onDeleteClick = onNoteDeleteClick,
                )

                DetailTab.Reviews -> TabReviews(
                    uiState = uiState,
                    onReviewClick = onReviewClick,
                )

                DetailTab.Suggestions -> TabSuggestions(
                    uiState = uiState.suggestionsUiState,
                    onBookClick = onSuggestedBookClick,
                )
            }
        }
    }
}

@Composable
private fun DeleteDialog(onDeleteDialogDismissed: () -> Unit, onDeleteConfirmClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDeleteDialogDismissed,
        icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
        title = {
            Text(text = stringResource(R.string.detail_title_delete))
        },
        text = {
            Text(
                stringResource(R.string.detail_desc_delete_msg),
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDeleteConfirmClick,
            ) {
                Text(stringResource(R.string.detail_btn_confirm_delete))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDeleteDialogDismissed,
            ) {
                Text(stringResource(R.string.detail_btn_cancel_delete))
            }
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BookDetailAppBar(
    uiState: BookDetailUiState,
    onNavigateBack: () -> Unit,
    onNewShelfClick: () -> Unit,
    onNewNoteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditBookClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = {},
        navigationIcon = {
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_navigate_back),
                )
            }
        },
        actions = {
            // Actions are visible only if the book is in the library (user's personal collection)
            if (uiState.isBookInLibrary) {
                when (uiState.selectedTab) {
                    DetailTab.Shelves -> {
                        IconButton(onClick = { onNewShelfClick() }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.content_add_new_shelf),
                            )
                        }
                    }

                    DetailTab.Notes -> {
                        IconButton(onClick = { onNewNoteClick() }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = stringResource(R.string.content_add_new_note),
                            )
                        }
                    }

                    DetailTab.Detail -> {
                        IconButton(
                            modifier = Modifier.testTag(BookDetailTestTag.BTN_DELETE_BOOK),
                            onClick = { onDeleteClick() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.content_desc_delete),
                            )
                        }
                        IconButton(
                            modifier = Modifier.testTag(BookDetailTestTag.BTN_EDIT_BOOK),
                            onClick = { onEditBookClick() },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.content_desc_edit),
                            )
                        }
                    }

                    DetailTab.Reviews -> {}
                    DetailTab.Suggestions -> {}
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyLibraryTheme {
        BookDetailScreen(
            uiState = BookDetailUiState(
                selectedTab = DetailTab.Detail,
                shelves = listOf(ShelfForBook(Shelf.Id("1"), "Test", true)),
                isBookDeleted = false,
                book = Book(
                    id = Book.Id("id"),
                    title = "Title",
                    author = "Tolkien",
                    description = "Desc",
                    isbn = "545454",
                    publisher = "",
                    yearPublished = 1989,
                    pageCount = 565,
                    thumbnailLink = null,
                    userRating = 5,
                    dateAdded = "1999",
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onEditBookClick = {},
            onDeleteClick = {},
            onNewShelfClick = {},
            onReviewClick = {},
            onShelfCheckedChange = { _, _ -> },
            onTabClick = {},
            onDeleteConfirmClick = {},
            onDeleteDialogDismissed = {},
            onNewNoteClick = {},
            onNoteDialogDismissed = {},
            onNoteDialogSaveClick = {},
            onNoteDialogUpdateClick = { _, _ -> },
            onNoteEditClick = { _ -> },
            onNoteDeleteClick = { _ -> },
        )
    }
}
