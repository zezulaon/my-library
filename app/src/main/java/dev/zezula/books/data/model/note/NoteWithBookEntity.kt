package dev.zezula.books.data.model.note

class NoteWithBookEntity(
    val id: String,
    val bookId: String,
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
