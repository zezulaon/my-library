package dev.zezula.books.data.database.entities

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.NoteWithBook

class NoteWithBookEntity(
    val id: Note.Id,
    val bookId: Book.Id,
    val dateAdded: String,
    val text: String,
    val page: Int? = null,
    val type: String? = null,
    val bookTitle: String? = null,
)

fun NoteWithBookEntity.asExternalModel(): NoteWithBook {
    return NoteWithBook(
        note = Note(
            id = this.id,
            bookId = this.bookId,
            dateAdded = this.dateAdded,
            text = this.text,
            page = this.page,
            type = this.type,
        ),
        bookTitle = this.bookTitle,
    )
}
