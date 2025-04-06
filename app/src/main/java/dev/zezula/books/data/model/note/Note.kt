package dev.zezula.books.data.model.note

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.util.formatDate

data class Note(
    val id: Id,
    val bookId: Book.Id,
    val dateAdded: String,
    val text: String,
    val page: Int? = null,
    val type: String? = null,
) {

    @JvmInline
    value class Id(val value: String)

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
        id = Note.Id("1"),
        bookId = Book.Id("1"),
        dateAdded = "2023-01-05T17:43:25.629",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget aliquam ultricies," +
            " nunc nisl ultricies nunc, quis aliquet nisl nunc quis nisl",
    ),
    Note(
        id = Note.Id("2"),
        bookId = Book.Id("2"),
        dateAdded = "2022-01-05T17:43:25.629",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget aliquam ultricies," +
            " nunc nisl ultricies nunc, quis aliquet nisl nunc quis nisl",
    ),
)
