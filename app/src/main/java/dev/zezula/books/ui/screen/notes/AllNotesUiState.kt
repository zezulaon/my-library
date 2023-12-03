package dev.zezula.books.ui.screen.notes

import dev.zezula.books.data.model.note.NoteWithBook

data class AllNotesUiState(
    val notes: List<NoteWithBook> = emptyList(),
    val errorMessage: Int? = null,
)
