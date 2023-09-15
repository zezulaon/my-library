package dev.zezula.books.ui.screen.list

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
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
import dev.zezula.books.util.homeBtnAddBookManually
import dev.zezula.books.util.isLastIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BookListRoute(
    viewModel: BookListViewModel,
    onAddBookClick: () -> Unit,
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
        onAddBookClick = onAddBookClick,
        onScanBookClick = onScanBookClick,
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
    onScanBookClick: () -> Unit,
    onBookClick: (String) -> Unit,
    onManageShelvesClick: () -> Unit,
    onAllBooksClick: () -> Unit,
    onShelfClick: (Shelf) -> Unit,
    modifier: Modifier = Modifier,
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
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
                    onScanBookClick = onScanBookClick,
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
    onScanBookClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, contentDescription = stringResource(id = R.string.content_open_drawer))
            }
            IconButton(onClick = onAddBookClick, modifier = Modifier.testTag(homeBtnAddBookManually)) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.content_add_new_book))
            }
        },
        floatingActionButton = {
            ScanBookButton(onButtonClick = onScanBookClick)
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
private fun ScanBookButton(onButtonClick: () -> Unit) {
    ExtendedFloatingActionButton(
        icon = { Icon(painter = painterResource(id = R.drawable.ic_barcode), contentDescription = null) },
        text = { Text(text = stringResource(R.string.home_btn_scan)) },
        onClick = onButtonClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun PreviewSportItemList() {
    MyLibraryTheme {
        BookListScreen(
            uiState = BookListUiState(books = previewBooks, emptyList(), null),
            onAddBookClick = {},
            onScanBookClick = {},
            onBookClick = {},
            onManageShelvesClick = {},
            onAllBooksClick = {},
            onShelfClick = {},
        )
    }
}
