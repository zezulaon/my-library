package dev.zezula.books.ui.screen.list

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.ui.theme.MyLibraryTheme
import dev.zezula.books.util.homeAppBar
import dev.zezula.books.util.homeBtnAddBook
import dev.zezula.books.util.homeBtnAddBookManually
import dev.zezula.books.util.homeBtnScanBarcode
import dev.zezula.books.util.isLastIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListRoute(
    viewModel: BookListViewModel,
    onAddBookManuallyClick: () -> Unit,
    onFindBookOnlineClick: () -> Unit,
    onScanBookClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onManageShelvesClick: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    uiState.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text, uiState) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    if (drawerState.isClosed && uiState.managedShelvesClicked) {
        val rememberUpdatedClick by rememberUpdatedState(onManageShelvesClick)
        LaunchedEffect(drawerState, uiState, viewModel) {
            viewModel.onManagedShelvesClickedHandled()
            rememberUpdatedClick()
        }
    }

    BookListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        scope = scope,
        onAddBookClick = {
            viewModel.onAddBookSheetOpenRequest()
        },
        onAddBookSheetCloseRequested = {
            viewModel.onAddBookSheetDismissRequest()
        },
        onScanBarcodeClick = onScanBookClick,
        onAddManuallyClick = onAddBookManuallyClick,
        onFindOnlineClick = onFindBookOnlineClick,

        onBookClick = onBookClick,
        onAllBooksClick = {
            scope.launch { drawerState.close() }
            viewModel.onAllBooksShelfSelected()
        },
        onManageShelvesClick = {
            scope.launch { drawerState.close() }
            viewModel.onManagedShelvesClicked()
        },
        onShelfClick = {
            scope.launch { drawerState.close() }
            viewModel.onShelfSelected(it)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun BookListScreen(
    uiState: BookListUiState,
    onAddBookClick: () -> Unit,
    onAddBookSheetCloseRequested: () -> Unit,
    onScanBarcodeClick: () -> Unit,
    onAddManuallyClick: () -> Unit,
    onFindOnlineClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onManageShelvesClick: () -> Unit,
    onAllBooksClick: () -> Unit,
    onShelfClick: (Shelf) -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                uiState = uiState,
                onManageShelvesClick = onManageShelvesClick,
                onAllBooksClick = onAllBooksClick,
                onShelfClick = onShelfClick,
            )
        },
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier,
            topBar = { BookListTopAppBar(uiState = uiState) },
            bottomBar = {
                BookListBottomBar(
                    onAddBookClick = onAddBookClick,
                    onMenuClick = { scope.launch { drawerState.open() } },
                )
            },
        ) { innerPadding ->
            BookList(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                books = uiState.books,
                onBookClick = onBookClick,
            )

            if (uiState.addBookSheetOpened) {
                AddBookBottomSheet(
                    uiState,
                    onAddBookSheetCloseRequested,
                    onScanBarcodeClick,
                    onAddManuallyClick,
                    onFindOnlineClick,
                    bottomSheetState,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AddBookBottomSheet(
    uiState: BookListUiState,
    onAddBookSheetCloseRequested: () -> Unit,
    onScanBarcodeClick: () -> Unit,
    onAddManuallyClick: () -> Unit,
    onFindOnlineClick: () -> Unit,
    bottomSheetState: SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = { onAddBookSheetCloseRequested() },
        sheetState = bottomSheetState,
        windowInsets = BottomSheetDefaults.windowInsets,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.home_btn_add_book), style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            ListItem(
                modifier = Modifier
                    .testTag(homeBtnAddBookManually)
                    .clickable {
                        onAddManuallyClick()
                        onAddBookSheetCloseRequested()
                    },
                headlineContent = { Text(stringResource(R.string.home_btn_add_manually)) },
                leadingContent = { Icon(Icons.Default.Create, contentDescription = null) },
            )
            ListItem(
                modifier = Modifier.clickable {
                    onFindOnlineClick()
                    onAddBookSheetCloseRequested()
                },
                headlineContent = { Text(stringResource(R.string.home_btn_find_online)) },
                leadingContent = { Icon(Icons.Default.Search, contentDescription = null) },
            )
            ListItem(
                modifier = Modifier
                    .testTag(homeBtnScanBarcode)
                    .clickable {
                        onScanBarcodeClick()
                        onAddBookSheetCloseRequested()
                    },
                headlineContent = { Text(stringResource(R.string.home_btn_scan_barcode)) },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.ic_barcode), contentDescription = null)
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookListTopAppBar(
    uiState: BookListUiState,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.testTag(homeAppBar),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = {
            Text(uiState.selectedShelf?.title ?: stringResource(R.string.home_shelf_title_all_books))
        },
    )
}

@Composable
private fun BookListBottomBar(
    onAddBookClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = stringResource(id = R.string.content_open_drawer))
            }
        },
        floatingActionButton = {
            AddBookButton(onButtonClick = onAddBookClick)
        },
    )
}

@Composable
private fun BookList(
    books: List<Book>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(key = { _, item -> item.id }, items = books) { index, book ->
            val isLast = books.isLastIndex(index)
            BookListItem(book = book, onBookClick = onBookClick, isLast = isLast)
        }
    }
}

@Composable
private fun AddBookButton(onButtonClick: () -> Unit) {
    ExtendedFloatingActionButton(
        modifier = Modifier.testTag(homeBtnAddBook),
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        text = { Text(text = stringResource(R.string.home_btn_add)) },
        onClick = onButtonClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewBookListScreen() {
    MyLibraryTheme {
        BookListScreen(
            uiState = BookListUiState(books = previewBooks, emptyList(), null),
            onAddBookClick = {},
            onAddManuallyClick = {},
            onFindOnlineClick = {},
            onScanBarcodeClick = {},
            onBookClick = {},
            onManageShelvesClick = {},
            onAllBooksClick = {},
            onShelfClick = {},
            onAddBookSheetCloseRequested = {},
        )
    }
}
