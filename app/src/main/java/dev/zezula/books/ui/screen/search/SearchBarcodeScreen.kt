package dev.zezula.books.ui.screen.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.ui.theme.MyLibraryTheme
import org.jetbrains.annotations.VisibleForTesting

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun SearchBarcodeRoute(
    viewModel: SearchBarcodeViewModel,
    onNavigateBack: () -> Unit,
    onScanAgainClick: () -> Unit,
    onBookFound: (bookId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.foundBookId?.let {
        val latestOnBookFound by rememberUpdatedState(onBookFound)
        LaunchedEffect(uiState) {
            latestOnBookFound(it)
        }
    }

    SearchBarcodeScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onScanAgainClick = onScanAgainClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun SearchBarcodeScreen(
    uiState: SearchBarcodeUiState,
    onNavigateBack: () -> Unit,
    onScanAgainClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.search_title)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_desc_navigate_back)
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isInProgress) {
                CircularProgressIndicator()
            }
            if (uiState.noBookFound) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.search_no_book_found_for_isbn))
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        text = uiState.barcode,
                        style = MaterialTheme.typography.titleLarge
                    )
                    FilledTonalButton(onClick = onScanAgainClick) {
                        Text(stringResource(R.string.search_btn_scan_again))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyLibraryTheme {
        SearchBarcodeScreen(
            uiState = SearchBarcodeUiState(noBookFound = true, barcode = "978555444777"),
            onScanAgainClick = {},
            onNavigateBack = {}
        )
    }
}