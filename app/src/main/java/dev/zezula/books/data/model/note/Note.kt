package dev.zezula.books.data.model.note

import dev.zezula.books.util.formatDate

data class Note(
    val id: String,
    val bookId: String,
    val dateAdded: String,
    val text: String,
    val page: Int? = null,
    val type: String? = null,
) {
    val dateAddedFormatted: String = formatDate(dateAdded)
}

data class NoteFormData(
    val text: String,
    val type: String? = null,
    val page: Int? = null,
    val dateAdded: String? = null,
)

val previewNotes = listOf(
    Note(
        id = "1",
        bookId = "1",
        dateAdded = "2023-01-05T17:43:25.629",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget aliquam ultricies," +
            " nunc nisl ultricies nunc, quis aliquet nisl nunc quis nisl",
    ),
    Note(
        id = "2",
        bookId = "2",
        dateAdded = "2022-01-05T17:43:25.629",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget aliquam ultricies," +
            " nunc nisl ultricies nunc, quis aliquet nisl nunc quis nisl",
    ),
)
