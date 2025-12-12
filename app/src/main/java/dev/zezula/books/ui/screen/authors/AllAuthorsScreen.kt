package dev.zezula.books.ui.screen.authors

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.core.model.AuthorAndBooks
import dev.zezula.books.ui.theme.MyLibraryTheme

@Composable
fun AllAuthorsRoute(
    viewModel: AllAuthorsViewModel,
    onAuthorClick: (authorNameId: String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AllAuthorsScreen(
        uiState = uiState,
        onAuthorClick = onAuthorClick,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
@VisibleForTesting
fun AllAuthorsScreen(
    uiState: AllAuthorsUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onAuthorClick: (authorNameId: String) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { AllAuthorsTopAppBar(uiState = uiState, onNavigateBack = onNavigateBack) },
    ) { innerPadding ->
        AllAuthorsList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            authors = uiState.authors,
            onAuthorClick = onAuthorClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllAuthorsTopAppBar(
    uiState: AllAuthorsUiState,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = { AllAuthorsAppBarTitle(uiState) },
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
private fun AllAuthorsAppBarTitle(uiState: AllAuthorsUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.screen_title_all_authors))
        val numberOfAuthors = uiState.authors.count()
        val noAuthorsFormatted = pluralStringResource(
            R.plurals.all_authors_number_of_authors_subtitle,
            numberOfAuthors,
            numberOfAuthors,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(noAuthorsFormatted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AllAuthorsList(
    authors: List<AuthorAndBooks>,
    modifier: Modifier = Modifier,
    onAuthorClick: (authorNameId: String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(key = { _, item -> item.authorNameId }, items = authors) { _, item ->
            ListItem(
                modifier = Modifier.clickable { onAuthorClick(item.authorNameId) },
                headlineContent = { Text(text = item.authorName, style = MaterialTheme.typography.bodyMedium) },
                trailingContent = {
                    Text(text = item.numberOfBooks.toString(), style = MaterialTheme.typography.labelSmall)
                },
            )
        }
    }
}

@Composable
@Preview
fun PreviewAllAuthorsScreen() {
    MyLibraryTheme {
        AllAuthorsScreen(
            uiState = AllAuthorsUiState(),
            onNavigateBack = {},
        )
    }
}
