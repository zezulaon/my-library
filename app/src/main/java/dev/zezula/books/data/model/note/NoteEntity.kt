package dev.zezula.books.data.model.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.BookEntity

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
    indices = [
        Index(value = ["bookId"]),
    ],
)
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    val dateAdded: String,
    val text: String,
    val page: Int? = null,
    val type: String? = null,
)

fun NoteEntity.asExternalModel(): Note {
    return Note(
        id = this.id,
        bookId = this.bookId,
        dateAdded = this.dateAdded,
        text = this.text,
        page = this.page,
        type = this.type,
    )
}

fun fromNetworkNote(
    networkNote: NetworkNote,
    bookId: String,
): NoteEntity {
    checkNotNull(networkNote.id) { "Note needs [id] property" }
    checkNotNull(networkNote.dateAdded) { "Note needs [dateAdded] property" }
    checkNotNull(networkNote.text) { "Note needs [text] property" }
    return NoteEntity(
        id = networkNote.id,
        bookId = bookId,
        dateAdded = networkNote.dateAdded,
        text = networkNote.text,
        page = networkNote.page,
        type = networkNote.type,
    )
}
