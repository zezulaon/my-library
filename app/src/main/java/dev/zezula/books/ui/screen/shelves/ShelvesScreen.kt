package dev.zezula.books.ui.screen.shelves

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.util.manageShelvesShelfItem

@Composable
fun ShelvesRoute(
    viewModel: ShelvesViewModel,
    onNavigateBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Launches SnackBar if the UI is in an error state
    uiState.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, text, viewModel) {
            snackbarHostState.showSnackbar(text, duration = SnackbarDuration.Long)
            viewModel.snackbarMessageShown()
        }
    }

    ShelvesListScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onAddShelfClick = { viewModel.onAddNewShelfClick() },
        onDeleteShelfClick = { viewModel.deleteShelf(it) },
        onEditShelfClick = { viewModel.onEditShelfClicked(it) },
        onAddShelfDialogSaveClick = { viewModel.createShelf(it) },
        onAddShelfDialogUpdateClick = { shelf, newTitle -> viewModel.updateShelf(shelf, newTitle) },
        onAddShelfDialogDismiss = { viewModel.onShelfDialogDismissed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShelvesListScreen(
    uiState: ShelvesUiState,
    onNavigateBack: () -> Unit,
    onAddShelfClick: () -> Unit,
    onDeleteShelfClick: (selectedShelf: Shelf) -> Unit,
    onEditShelfClick: (selectedShelf: Shelf) -> Unit,
    onAddShelfDialogSaveClick: (shelfTitle: String) -> Unit,
    onAddShelfDialogUpdateClick: (shelf: Shelf, updatedShelfTitle: String) -> Unit,
    onAddShelfDialogDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
        topBar = { ShelvesListTopAppBar(onNavigateBack = onNavigateBack) },
        bottomBar = { ShelvesBottomBar(onAddShelfClick = onAddShelfClick) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            ShelvesList(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                shelves = uiState.shelves,
                onEditShelfClick = onEditShelfClick,
                onDeleteClick = onDeleteShelfClick,
            )

            if (uiState.showAddOrEditShelfDialog) {
                AddOrEditShelfDialog(
                    selectedShelf = uiState.selectedShelf,
                    onDialogDismiss = onAddShelfDialogDismiss,
                    onDialogSaveClick = onAddShelfDialogSaveClick,
                    onDialogUpdateClick = onAddShelfDialogUpdateClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShelvesListTopAppBar(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(stringResource(R.string.shelves_screen_title)) },
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
}

@Composable
private fun ShelvesBottomBar(
    onAddShelfClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomAppBar(
        modifier = modifier,
        floatingActionButton = {
            NewShelfButton(onButtonClick = onAddShelfClick)
        },
        actions = {},
    )
}

@Composable
private fun ShelvesList(
    shelves: List<Shelf>,
    onEditShelfClick: (Shelf) -> Unit,
    onDeleteClick: (Shelf) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandedShelfItem = remember { mutableStateOf<Shelf?>(null) }
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(items = shelves, key = { _, item -> item.id }) { _, shelf ->
            val isExpanded = expandedShelfItem.value == shelf
            ShelfListItem(
                modifier = Modifier.testTag(manageShelvesShelfItem),
                shelf = shelf,
                onDeleteClick = onDeleteClick,
                onEditShelfClick = onEditShelfClick,
                onExpandClick = { expandedShelfItem.value = if (isExpanded) null else shelf },
                isExpanded = expandedShelfItem.value == shelf,
            )
        }
    }
}

@Composable
fun NewShelfButton(onButtonClick: () -> Unit) {
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Filled.Add, null) },
        text = { Text(text = stringResource(R.string.shelves_btn_new_shelf)) },
        onClick = onButtonClick,
    )
}
