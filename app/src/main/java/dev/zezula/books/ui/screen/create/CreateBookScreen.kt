package dev.zezula.books.ui.screen.create

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.testtag.BookEditorTestTag
import dev.zezula.books.ui.theme.MyLibraryTheme
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CreateBookRoute(
    viewModel: CreateBookViewModel,
    onNavigateBack: () -> Unit,
    onItemSavedSuccess: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            Timber.d("Lifecycle event: $event")
            if (event == Lifecycle.Event.ON_CREATE) {
                viewModel.loadBook()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (uiState.isBookSaved) {
        val latestOnItemSavedSuccess by rememberUpdatedState(onItemSavedSuccess)
        LaunchedEffect(uiState) {
            latestOnItemSavedSuccess()
        }
    }

    uiState.errorMessage?.let { msg ->
        val text = stringResource(msg)
        LaunchedEffect(snackbarHostState, viewModel, msg, text) {
            snackbarHostState.showSnackbar(text)
            viewModel.snackbarMessageShown()
        }
    }

    CreateBookScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onSaveButtonClick = {
            keyboardController?.hide()
            viewModel.saveBook()
        },
        onTitleValueChanged = { viewModel.updateTitle(it) },
        onIsbnValueChanged = { viewModel.updateIsbn(it) },
        onDescValueChanged = { viewModel.updateDescription(it) },
        onAuthorValueChanged = { viewModel.updateAuthor(it) },
        onPublisherValueChanged = { viewModel.updatePublisher(it) },
        onYearPublishedValueChanged = { viewModel.updateYearPublished(it) },
        onPageCountValueChanged = { viewModel.updatePageCount(it) },
        onRatingStarSelected = { viewModel.updateRating(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun CreateBookScreen(
    uiState: CreateBookUiState,
    onNavigateBack: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onTitleValueChanged: (String) -> Unit,
    onDescValueChanged: (String) -> Unit,
    onIsbnValueChanged: (String) -> Unit,
    onAuthorValueChanged: (String) -> Unit,
    onPublisherValueChanged: (String) -> Unit,
    onYearPublishedValueChanged: (String) -> Unit,
    onPageCountValueChanged: (String) -> Unit,
    onRatingStarSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier.testTag(BookEditorTestTag.ROOT),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    val saveBtnRes = if (uiState.isInEditMode) {
                        R.string.screen_title_update_book
                    } else {
                        R.string.screen_title_create_new_book
                    }
                    Text(stringResource(saveBtnRes))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
                actions = {
                    TextButton(
                        modifier = Modifier.testTag(BookEditorTestTag.BTN_SAVE),
                        onClick = onSaveButtonClick,
                        enabled = uiState.isInProgress.not(),
                    ) {
                        val titleRes = if (uiState.isInEditMode) R.string.btn_update else R.string.btn_add
                        Text(text = stringResource(titleRes))
                    }
                },
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            if (uiState.isInProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            InputDataForm(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                uiState = uiState,
                onTitleValueChanged = onTitleValueChanged,
                onIsbnValueChanged = onIsbnValueChanged,
                onDescValueChanged = onDescValueChanged,
                onAuthorValueChanged = onAuthorValueChanged,
                onPublisherValueChanged = onPublisherValueChanged,
                onYearPublishedValueChanged = onYearPublishedValueChanged,
                onPageCountValueChanged = onPageCountValueChanged,
                onRatingStarSelected = onRatingStarSelected,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyLibraryTheme {
        CreateBookScreen(
            uiState = CreateBookUiState(
                BookFormData(
                    title = "Hobit",
                    author = "J. R. R. Tolkien",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing e aliquip ex ea commodo consuat.",
                ),
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onSaveButtonClick = {},
            onTitleValueChanged = {},
            onDescValueChanged = {},
            onIsbnValueChanged = {},
            onAuthorValueChanged = {},
            onPublisherValueChanged = {},
            onPageCountValueChanged = {},
            onYearPublishedValueChanged = {},
            onRatingStarSelected = {},
        )
    }
}
