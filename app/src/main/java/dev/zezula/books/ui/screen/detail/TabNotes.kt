package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zezula.books.R
import dev.zezula.books.core.model.Note

@Composable
fun TabNotes(
    uiState: BookDetailUiState,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        if (uiState.notes.isEmpty()) {
            NoNotesInfoCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            )
        } else {
            val expandedNoteItem = remember { mutableStateOf<Note?>(null) }
            LazyColumn(
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(items = uiState.notes, key = { note -> note.id.value }) { note ->
                    val isExpanded = expandedNoteItem.value == note
                    NoteListItem(
                        note = note,
                        isExpanded = isExpanded,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        onExpandClick = { expandedNoteItem.value = if (isExpanded) null else note },
                    )
                }
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
                text = stringResource(R.string.detail_notes_empty_info_card_content),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
