package dev.zezula.books.ui.screen.notes

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.note.NoteWithBook
import dev.zezula.books.ui.theme.MyLibraryTheme
import dev.zezula.books.util.allNotesAppBar

@Composable
fun AllNotesRoute(
    viewModel: AllNotesViewModel,
    onNoteClick: (bookId: Book.Id) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AllNotesScreen(
        uiState = uiState,
        onNoteClick = onNoteClick,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
@VisibleForTesting
fun AllNotesScreen(
    uiState: AllNotesUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNoteClick: (bookId: Book.Id) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = { AllNotesTopAppBar(uiState = uiState, onNavigateBack = onNavigateBack) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.notes.isEmpty()) {
                NoNotesInfoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                )
            } else {
                AllNotesList(
                    modifier = Modifier.fillMaxWidth(),
                    notes = uiState.notes,
                    onNoteClick = onNoteClick,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllNotesTopAppBar(
    uiState: AllNotesUiState,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.testTag(allNotesAppBar),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        title = { AllNotesAppBarTitle(uiState) },
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
private fun AllNotesAppBarTitle(uiState: AllNotesUiState) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(R.string.screen_title_all_notes))
        val numberOfNotes = uiState.notes.count()
        val noNotesFormatted = pluralStringResource(
            R.plurals.all_notes_number_of_notes_subtitle,
            numberOfNotes,
            numberOfNotes,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(noNotesFormatted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun AllNotesList(
    notes: List<NoteWithBook>,
    modifier: Modifier = Modifier,
    onNoteClick: (bookId: Book.Id) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(key = { _, item -> item.note.id.value }, items = notes) { index, (note, title) ->
            var footNote = title ?: "-"
            if (note.page != null) {
                footNote = "$footNote, page: ${note.page}"
            }
            ListItem(
                modifier = Modifier.clickable { onNoteClick(note.bookId) },
                headlineContent = { Text(text = note.text, style = MaterialTheme.typography.bodyMedium) },
                overlineContent = { Text(text = note.dateAddedFormatted, style = MaterialTheme.typography.bodySmall) },
                supportingContent = { Text(text = footNote, style = MaterialTheme.typography.bodySmall) },
            )
            if (notes.lastIndex != index) {
                HorizontalDivider(thickness = 1.dp)
            }
        }
    }
}

@Composable
fun NoNotesInfoCard(
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
                text = stringResource(R.string.notes_empty_info_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.padding(top = 0.dp),
                text = stringResource(R.string.notes_empty_info_card_content),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview
fun PreviewAllNotesScreen() {
    MyLibraryTheme {
        AllNotesScreen(
            uiState = AllNotesUiState(),
            onNavigateBack = {},
        )
    }
}
