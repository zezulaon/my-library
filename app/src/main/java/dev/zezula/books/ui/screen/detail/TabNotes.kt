package dev.zezula.books.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zezula.books.data.model.note.Note

@Composable
fun TabNotes(
    uiState: BookDetailUiState,
    onEditClick: (Note) -> Unit,
    onDeleteClick: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    val expandedNoteItem = remember { mutableStateOf<Note?>(null) }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = uiState.notes, key = { note -> note.id }) { note ->
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
