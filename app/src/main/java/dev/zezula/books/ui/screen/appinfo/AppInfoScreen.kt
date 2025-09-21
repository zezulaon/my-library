package dev.zezula.books.ui.screen.appinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.BuildConfig
import dev.zezula.books.R
import dev.zezula.books.core.utils.shortUserId
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun AppInfoRoute(
    viewModel: AppInfoViewModel,
    onContactUsClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AppInfoScreen(
        uiState = uiState,
        onContactUsClicked = onContactUsClicked,
        onReleaseNotesClicked = onReleaseNotesClicked,
        onExportClicked = {
            viewModel.onExportClicked()
        },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppInfoScreen(
    uiState: AppInfoUiState,
    onContactUsClicked: () -> Unit,
    onReleaseNotesClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.app_name),
                        )
                        val versionValue = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE}, ${shortUserId()})"
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = stringResource(id = R.string.app_version, versionValue),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            ListItem(
                modifier = Modifier
                    .clickable { onReleaseNotesClicked() },
                headlineContent = { Text(stringResource(R.string.about_btn_release_notes)) },
                trailingContent = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
            )
            ListItem(
                modifier = Modifier
                    .clickable { onContactUsClicked() },
                headlineContent = { Text(stringResource(R.string.about_btn_contact)) },
                trailingContent = {
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
            ) {
                Text("Export", style = MaterialTheme.typography.labelSmall)
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.app_more_info_export_text))
                Text(
                    text = uiState.exportDir.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                )
                if (uiState.exportInProgress) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(R.string.app_more_info_export_in_progress_label),
                        )
                    }
                } else {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.app_more_info_export_dir),
                        )
                        uiState.lastExportedFiles.forEach { fileName ->
                            Text(
                                text = fileName,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(8.dp),
                        onClick = {
                            onExportClicked()
                        },
                    ) {
                        Text("Export")
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewAppInfoScreen() {
    MyLibraryTheme {
        AppInfoScreen(
            uiState = AppInfoUiState(
                exportInProgress = false,
                exportDir = "/dir/test/android/com.package/export",
                lastExportedFiles = emptyList(),
            ),
            onContactUsClicked = {},
            onReleaseNotesClicked = {},
            onExportClicked = {},
            onNavigateBack = {},
        )
    }
}

@Composable
@Preview
fun PreviewAppInfoScreenExportInProgress() {
    MyLibraryTheme {
        AppInfoScreen(
            uiState = AppInfoUiState(
                exportInProgress = true,
                exportDir = null,
                lastExportedFiles = emptyList(),
            ),
            onContactUsClicked = {},
            onReleaseNotesClicked = {},
            onExportClicked = {},
            onNavigateBack = {},
        )
    }
}

@Composable
@Preview
fun PreviewAppInfoScreenLastExportedProgress() {
    MyLibraryTheme {
        AppInfoScreen(
            uiState = AppInfoUiState(
                exportInProgress = false,
                exportDir = null,
                lastExportedFiles = listOf("file1.txt", "file2.txt", "file3.txt"),
            ),
            onContactUsClicked = {},
            onReleaseNotesClicked = {},
            onExportClicked = {},
            onNavigateBack = {},
        )
    }
}
