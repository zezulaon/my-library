package dev.zezula.books.ui.screen.search

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.zezula.books.R
import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.previewBooks
import dev.zezula.books.testtag.ScanBarcodeTestTag
import dev.zezula.books.ui.screen.components.BookListItem
import dev.zezula.books.ui.screen.scanner.IsbnScannerComponent
import dev.zezula.books.ui.theme.MyLibraryTheme
import org.jetbrains.annotations.VisibleForTesting

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SearchBarcodeRoute(
    isBulkScanningEnabled: Boolean,
    viewModel: SearchBarcodeViewModel,
    onNavigateBack: () -> Unit,
    onBookFound: (bookId: Book.Id) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val foundBookId = uiState.foundedBookId
    if (isBulkScanningEnabled.not() && foundBookId != null) {
        // If we are not in bulk scan mode, we can navigate directly to the book detail screen
        val latestOnBookFound by rememberUpdatedState(onBookFound)
        LaunchedEffect(uiState) {
            latestOnBookFound(foundBookId)
        }
    }

    SearchBarcodeScreen(
        isBulkScanningEnabled = isBulkScanningEnabled,
        uiState = uiState,
        isCameraPermissionGranted = cameraPermissionState.status.isGranted,
        onRequestCameraPermissionClick = { cameraPermissionState.launchPermissionRequest() },
        onIsbnScanned = { isbn ->
            viewModel.onIsbnScanned(isbn)
        },
        onNavigateBack = onNavigateBack,
        onScanAgainClick = { viewModel.scanAgain() },
        onCancelScanClick = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun SearchBarcodeScreen(
    isBulkScanningEnabled: Boolean,
    uiState: SearchBarcodeUiState,
    isCameraPermissionGranted: Boolean,
    onRequestCameraPermissionClick: () -> Unit,
    onIsbnScanned: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onScanAgainClick: () -> Unit,
    onCancelScanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag(ScanBarcodeTestTag.ROOT),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search_title)) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.scannedIsbn == null) {
                // Launch the scanner if we don't have any scanned ISBN yet
                IsbnScanner(isCameraPermissionGranted, onIsbnScanned, onRequestCameraPermissionClick)
            } else if (uiState.isSearchInProgress) {
                CircularProgressIndicator()
            } else if (uiState.noBookFound) {
                NoBookFoundInfo(uiState.scannedIsbn, onScanAgainClick = onScanAgainClick)
            } else if (isBulkScanningEnabled && uiState.foundedBookId != null) {
                BulkScanInProgressCard(uiState, onCancelScanClick, onScanAgainClick)
            }
        }
    }
}

@Composable
private fun IsbnScanner(
    isCameraPermissionGranted: Boolean,
    onIsbnScanned: (String) -> Unit,
    onRequestCameraPermissionClick: () -> Unit,
) {
    if (isCameraPermissionGranted) {
        IsbnScannerComponent(onIsbnScanned)
    } else {
        RequestCameraPermission(onRequestCameraPermissionClick)
    }
}

@Composable
private fun RequestCameraPermission(onRequestCameraPermissionClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.scanner_perm_required))
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRequestCameraPermissionClick) {
            Text(stringResource(R.string.scanner_btn_allow_camera))
        }
    }
}

@Composable
private fun NoBookFoundInfo(
    scannedIsbn: String,
    onScanAgainClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(R.string.search_no_book_found_for_isbn))
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = scannedIsbn,
            style = MaterialTheme.typography.titleLarge,
        )
        FilledTonalButton(onClick = onScanAgainClick) {
            Text(stringResource(R.string.search_btn_scan_again))
        }
    }
}

@Composable
private fun BulkScanInProgressCard(
    uiState: SearchBarcodeUiState,
    onCancelScanClick: () -> Unit,
    onScanAgainClick: () -> Unit,
) {
    Column {
        val book = uiState.foundedBook
        val defaultShelf = stringResource(R.string.drawer_item_all_books)
        val shelf = uiState.selectedShelf?.title ?: defaultShelf
        if (book != null) {
            val label = stringResource(R.string.search_book_added_appendable_label)
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(book.title.orEmpty())
                    }
                    append(" ")
                    append(label)
                    append(" ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(shelf)
                    }
                    append(".")
                },
            )
            BookListItem(
                modifier = Modifier.padding(top = 8.dp),
                book = book,
                onBookClick = {},
                isLast = true,
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 24.dp),
        ) {
            TextButton(onClick = onCancelScanClick) {
                Text(stringResource(R.string.search_btn_cancel_scan))
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(onClick = onScanAgainClick) {
                Text(stringResource(R.string.search_btn_scan_another))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyLibraryTheme {
        SearchBarcodeScreen(
            isBulkScanningEnabled = true,
            uiState = SearchBarcodeUiState(
                noBookFound = false,
                foundedBookId = Book.Id("xxx"),
                scannedIsbn = "978555444777",
                foundedBook = previewBooks.first(),
            ),
            onScanAgainClick = {},
            onNavigateBack = {},
            onIsbnScanned = {},
            isCameraPermissionGranted = true,
            onRequestCameraPermissionClick = {},
            onCancelScanClick = {},
        )
    }
}
