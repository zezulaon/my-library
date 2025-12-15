package dev.zezula.books.ui.screen.list

import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.MigrationProgress
import dev.zezula.books.core.model.MigrationType
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.domain.repositories.SortBooksBy
import dev.zezula.books.testtag.HomeTestTag
import dev.zezula.books.ui.screen.components.BookList
import dev.zezula.books.ui.screen.signin.SignInUiState
import dev.zezula.books.ui.screen.signin.SignInViewModel
import dev.zezula.books.ui.theme.MyLibraryTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListRoute(
    viewModel: BookListViewModel,
    signInViewModel: SignInViewModel,
    onGoogleSignIn: () -> Unit,
    onAddBookManuallyClick: () -> Unit,
    onFindBookOnlineClick: () -> Unit,
    onScanBookClick: () -> Unit,
    onBulkScanBooksClick: (shelfId: Shelf.Id?) -> Unit,
    onBookClick: (Book.Id) -> Unit,
    onManageShelvesClick: () -> Unit,
    onAllAuthorsShelvesClick: () -> Unit,
    onAllNotesClick: () -> Unit,
    onSearchMyLibraryClick: () -> Unit,
    onMoreClicked: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val signUiState by signInViewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            Timber.d("Lifecycle event: $event")
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    uiState.infoMessages.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text, uiState) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    signUiState.uiMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text, signUiState) {
            snackbarHostState.showSnackbar(text)
            signInViewModel.snackbarMessageShown()
        }
    }

    val drawerItemClick = uiState.drawerNavigation.drawerItemClicked
    if (drawerState.isClosed && drawerItemClick != null) {
        val rememberOnManageShelvesClick by rememberUpdatedState(onManageShelvesClick)
        val rememberOnAllAuthorsClick by rememberUpdatedState(onAllAuthorsShelvesClick)
        val rememberOnAllNotesClick by rememberUpdatedState(onAllNotesClick)
        LaunchedEffect(drawerState, uiState, viewModel) {
            viewModel.onDrawerItemClickedHandled()
            when (drawerItemClick) {
                DrawerClickItem.MANAGED_SHELVES -> rememberOnManageShelvesClick()
                DrawerClickItem.ALL_AUTHORS -> rememberOnAllAuthorsClick()
                DrawerClickItem.ALL_NOTES -> rememberOnAllNotesClick()
            }
        }
    }

    BookListScreen(
        uiState = uiState,
        signUiState = signUiState,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        scope = scope,
        onAddBookClick = { viewModel.onAddBookSheetOpenRequest() },
        onAddBookSheetCloseRequested = { viewModel.onAddBookSheetDismissRequest() },
        onScanBarcodeClick = { onScanBookClick() },
        onBulkScanBooksClick = { onBulkScanBooksClick(uiState.selectedShelf?.id) },
        onAddManuallyClick = onAddBookManuallyClick,
        onFindOnlineClick = onFindBookOnlineClick,

        onBookClick = onBookClick,
        onAllBooksClick = {
            scope.launch { drawerState.close() }
            viewModel.onAllBooksShelfSelected()
        },
        onAllAuthorsClick = {
            scope.launch { drawerState.close() }
            viewModel.onDrawerItemClicked(DrawerClickItem.ALL_AUTHORS)
        },
        onAllNotesClick = {
            scope.launch { drawerState.close() }
            viewModel.onDrawerItemClicked(DrawerClickItem.ALL_NOTES)
        },
        onManageShelvesClick = {
            scope.launch { drawerState.close() }
            viewModel.onDrawerItemClicked(DrawerClickItem.MANAGED_SHELVES)
        },
        onShelfClick = {
            scope.launch { drawerState.close() }
            viewModel.onShelfSelected(it)
        },
        onMoreClicked = onMoreClicked,
        onSortBooksClick = { viewModel.onSortBooksClicked() },
        onSortDialogDismissRequested = { viewModel.onSortDialogDismissRequest() },
        onSortSelected = { viewModel.onSortBooksSelected(it) },
        onSearchMyLibraryClick = onSearchMyLibraryClick,
        onAnonymUpgradeSignUpClick = onGoogleSignIn,
        onAnonymUpgradeDismissClick = { signInViewModel.onAnonymUpgradeDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun BookListScreen(
    uiState: BookListUiState,
    signUiState: SignInUiState,
    modifier: Modifier = Modifier,
    onAddBookClick: () -> Unit = {},
    onAddBookSheetCloseRequested: () -> Unit = {},
    onScanBarcodeClick: () -> Unit = {},
    onBulkScanBooksClick: () -> Unit = {},
    onAddManuallyClick: () -> Unit = {},
    onFindOnlineClick: () -> Unit = {},
    onBookClick: (Book.Id) -> Unit = {},
    onManageShelvesClick: () -> Unit = {},
    onAllBooksClick: () -> Unit = {},
    onAllAuthorsClick: () -> Unit = {},
    onAllNotesClick: () -> Unit = {},
    onShelfClick: (Shelf) -> Unit = {},
    onMoreClicked: () -> Unit = {},
    onSortBooksClick: () -> Unit = {},
    onSortDialogDismissRequested: () -> Unit = {},
    onSortSelected: (SortBooksBy) -> Unit = {},
    onSearchMyLibraryClick: () -> Unit = {},
    onAnonymUpgradeDismissClick: () -> Unit = {},
    onAnonymUpgradeSignUpClick: () -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    if (uiState.sorting.sortDialogDisplayed) {
        SortBooksDialog(
            uiState = uiState,
            onDismissRequested = onSortDialogDismissRequested,
            onSortSelected = onSortSelected,
        )
    }
    ModalNavigationDrawer(
        modifier = Modifier.testTag(HomeTestTag.ROOT),
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                uiState = uiState,
                onManageShelvesClick = onManageShelvesClick,
                onAllBooksClick = onAllBooksClick,
                onShelfClick = onShelfClick,
                onAllAuthorsClick = onAllAuthorsClick,
                onAllNotesClick = onAllNotesClick,
            )
        },
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier,
            topBar = {
                BookListTopAppBar(
                    uiState = uiState,
                    onMoreClicked = onMoreClicked,
                )
            },
            bottomBar = {
                BookListBottomBar(
                    onAddBookClick = onAddBookClick,
                    onOpenDrawerClick = { scope.launch { drawerState.open() } },
                    onSortBooksClick = onSortBooksClick,
                    onSearchMyLibraryClick = onSearchMyLibraryClick,
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
            ) {
                if (signUiState.isSignInProgress || uiState.migrationProgress != null) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Column {
                    if (uiState.migrationProgress != null) {
                        MigrationCard(uiState.migrationProgress)
                    }

                    if (signUiState.anonymUpgradeRequired) {
                        UpgradeAnonymCard(
                            signUiState = signUiState,
                            onAnonymUpgradeDismissClick = onAnonymUpgradeDismissClick,
                            onAnonymUpgradeSignUpClick = onAnonymUpgradeSignUpClick,
                        )
                    }
                    if (uiState.books?.isEmpty() == true) {
                        NoBooksInfoCard(modifier = Modifier.padding(24.dp))
                    } else {
                        BookList(
                            modifier = Modifier.fillMaxWidth(),
                            books = uiState.books.orEmpty(),
                            onBookClick = onBookClick,
                        )
                    }
                }
            }

            if (uiState.infoMessages.addBookSheetOpened) {
                AddBookBottomSheet(
                    uiState = uiState,
                    onAddBookSheetCloseRequested,
                    onScanBarcodeClick,
                    onBulkScanBooksClick,
                    onAddManuallyClick,
                    onFindOnlineClick,
                    bottomSheetState,
                )
            }
        }
    }
}

@Composable
private fun MigrationCard(progress: MigrationProgress) {
    val title = when (progress.type) {
        MigrationType.BOOKS -> "Importing Books"
        MigrationType.SHELVES -> "Importing Shelves"
        MigrationType.GROUPING -> "Updating Shelves"
        MigrationType.COMMENTS -> "Updating Comments and Quotations"
    }
    val progressValue = if (progress.total == 0) {
        "Please wait..."
    } else {
        "${progress.current}/${progress.total}"
    }

    Card(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = Icons.Rounded.Info,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = progressValue,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun NoBooksInfoCard(
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
                text = stringResource(R.string.home_no_books_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = buildAnnotatedString {
                    append(stringResource(R.string.home_no_books_card_content_start))
                    append("\n\n")
                    InfoPoint(
                        title = R.string.home_no_books_card_content_point_1_title,
                        text = R.string.home_no_books_card_content_point_1_text,
                    )
                    InfoPoint(
                        title = R.string.home_no_books_card_content_point_2_title,
                        text = R.string.home_no_books_card_content_point_2_text,
                    )
                    InfoPoint(
                        title = R.string.home_no_books_card_content_point_3_title,
                        text = R.string.home_no_books_card_content_point_3_text,
                    )
                    append("\n")
                    append(stringResource(R.string.home_no_books_card_content_end))
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AnnotatedString.Builder.InfoPoint(@StringRes title: Int, @StringRes text: Int) {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
        append(stringResource(title))
    }
    append(": ")
    append(stringResource(text))
    append("\n")
}

@Composable
private fun UpgradeAnonymCard(
    signUiState: SignInUiState,
    onAnonymUpgradeDismissClick: () -> Unit,
    onAnonymUpgradeSignUpClick: () -> Unit,
) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally),
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.upgrade_anonymous_text),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.align(Alignment.End)) {
                TextButton(onClick = onAnonymUpgradeDismissClick) {
                    Text(stringResource(R.string.upgrade_anonymous_btn_dismiss))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    enabled = signUiState.isSignInProgress.not(),
                    onClick = onAnonymUpgradeSignUpClick,
                ) {
                    Text(stringResource(R.string.upgrade_anonymous_btn_sign_up))
                }
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
    onBulkScanBooksClick: () -> Unit,
    onAddManuallyClick: () -> Unit,
    onFindOnlineClick: () -> Unit,
    bottomSheetState: SheetState,
) {
    ModalBottomSheet(
        onDismissRequest = { onAddBookSheetCloseRequested() },
        sheetState = bottomSheetState,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.home_btn_add_book), style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .padding(bottom = 54.dp),
        ) {
            val defaultShelf = stringResource(R.string.drawer_item_all_books)
            val selectedShelf = uiState.selectedShelf?.title ?: defaultShelf
            val label = stringResource(R.string.home_btn_bulk_scan_barcodes)
            ListItem(
                modifier = Modifier
                    .clickable {
                        onBulkScanBooksClick()
                        onAddBookSheetCloseRequested()
                    },
                headlineContent = {
                    Text(
                        text = buildAnnotatedString {
                            append(label)
                            append(" ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(selectedShelf)
                            }
                        },
                    )
                },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.ic_barcode), contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier
                    .testTag(HomeTestTag.BTN_SCAN_BARCODE)
                    .clickable {
                        onScanBarcodeClick()
                        onAddBookSheetCloseRequested()
                    },
                headlineContent = { Text(stringResource(R.string.home_btn_scan_barcode)) },
                leadingContent = {
                    Icon(painter = painterResource(id = R.drawable.ic_barcode), contentDescription = null)
                },
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
                    .testTag(HomeTestTag.BTN_ADD_BOOK_MANUALLY)
                    .clickable {
                        onAddManuallyClick()
                        onAddBookSheetCloseRequested()
                    },
                headlineContent = { Text(stringResource(R.string.home_btn_add_manually)) },
                leadingContent = { Icon(Icons.Default.Create, contentDescription = null) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookListTopAppBar(
    uiState: BookListUiState,
    onMoreClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = {
            HomeAppBarTitle(uiState)
        },
        actions = {
            IconButton(onClick = onMoreClicked) {
                Icon(Icons.Filled.MoreVert, contentDescription = null)
            }
        },
    )
}

@Composable
private fun HomeAppBarTitle(uiState: BookListUiState) {
    Column(
        modifier = Modifier.testTag(HomeTestTag.CONTAINER_TOOLBAR),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(uiState.selectedShelf?.title ?: stringResource(R.string.home_shelf_title_all_books))
        val numberOfBooks = uiState.books?.count() ?: 0
        val numberBooksFormatted = pluralStringResource(
            R.plurals.home_number_of_books_subtitle,
            numberOfBooks,
            numberOfBooks,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(numberBooksFormatted, style = MaterialTheme.typography.bodySmall)
            if (numberOfBooks > 1) {
                val sortType = when (uiState.sorting.sortBooksBy) {
                    SortBooksBy.TITLE -> stringResource(R.string.sort_books_by_label_title, numberBooksFormatted)
                    SortBooksBy.AUTHOR -> stringResource(R.string.sort_books_by_label_author, numberBooksFormatted)
                    SortBooksBy.DATE_ADDED -> stringResource(R.string.sort_books_by_label_date, numberBooksFormatted)
                    SortBooksBy.USER_RATING -> stringResource(R.string.sort_books_by_label_rating, numberBooksFormatted)
                }
                Text(", ", style = MaterialTheme.typography.bodySmall)
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.ic_sort_books),
                    contentDescription = null,
                )
                Text(sortType, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun BookListBottomBar(
    onAddBookClick: () -> Unit,
    onOpenDrawerClick: () -> Unit,
    onSortBooksClick: () -> Unit,
    onSearchMyLibraryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            IconButton(
                modifier = Modifier.testTag(HomeTestTag.BTN_OPEN_NAV_DRAWER),
                onClick = onOpenDrawerClick,
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_shelves),
                    contentDescription = stringResource(id = R.string.content_open_drawer),
                )
            }
            IconButton(onClick = onSearchMyLibraryClick) {
                Icon(Icons.Filled.Search, contentDescription = null)
            }
            IconButton(onClick = onSortBooksClick) {
                Icon(painterResource(id = R.drawable.ic_sort_books), contentDescription = null)
            }
        },
        floatingActionButton = {
            AddBookButton(onButtonClick = onAddBookClick)
        },
    )
}

@Composable
private fun AddBookButton(onButtonClick: () -> Unit) {
    ExtendedFloatingActionButton(
        modifier = Modifier.testTag(HomeTestTag.BTN_ADD_BOOK),
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
            uiState = BookListUiState(
                books = previewBooks,
                drawerNavigation = DrawerNavigationState(),
                sorting = SortingState(),
                migrationProgress = MigrationProgress(
                    type = MigrationType.BOOKS,
                    current = 1,
                    total = 10,
                ),
            ),
            signUiState = SignInUiState(),
        )
    }
}

@Composable
@Preview
fun PreviewUpgradeAnonymCard() {
    MyLibraryTheme {
        UpgradeAnonymCard(SignInUiState(), {}, {})
    }
}
