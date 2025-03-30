package dev.zezula.books.data.model.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.BookEntity
import kotlinx.datetime.Instant

/**
 * Represents a note entity in the database.
 *
 * Each note is linked to a specific book and can contain various information such as the user's thoughts,
 * favorite quotes, or specific page references. This entity is part of the app's data layer and is
 * stored in the 'notes' table within the local database.
 *
 * @property id Unique identifier for the note.
 * @property bookId Identifier of the book this note is associated with.
 * @property dateAdded The date when the note was added.
 * @property text The content of the note, containing user thoughts, quotes, etc.
 * @property page (Optional) The page number in the book that this note references, if applicable.
 * @property type (Optional) The type of the note (this is reserved for future use and is currently not used).
 */
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = CASCADE),
    ],
    indices = [
        // Index on [bookId] column is used to improve the performance of queries that filter by [bookId]
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
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isPendingSync: Boolean = false,
    @ColumnInfo(defaultValue = "0", typeAffinity = ColumnInfo.INTEGER)
    val isDeleted: Boolean = false,
    val lastModifiedTimestamp: String? = null,
)

fun NoteEntity.asNetworkNote(): NetworkNote {
    return NetworkNote(
        id = id,
        bookId = bookId,
        dateAdded = dateAdded,
        text = text,
        page = page,
        type = type,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

val previewNoteEntities = listOf(
    NoteEntity(
        id = "1",
        bookId = "101",
        dateAdded = "2021-01-01T00:00:00",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec euismod, nisl eget ultricies ultricni.",
        page = 1,
        type = "NOTE",
        lastModifiedTimestamp = "2021-01-01T00:00:00",
    ),
    NoteEntity(
        id = "2",
        bookId = "102",
        dateAdded = "2021-01-01T00:00:00",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vitae aliquet nisl nunc vitae nisl.",
        page = 1,
        type = "QUOTE",
        lastModifiedTimestamp = "2021-01-01T00:00:00",
    ),
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

fun fromNoteFormData(
    noteId: String,
    bookId: String,
    noteFormData: NoteFormData,
    dateAdded: String,
    lastModifiedTimestamp: Instant,
): NoteEntity {
    return NoteEntity(
        id = noteId,
        bookId = bookId,
        dateAdded = dateAdded,
        text = noteFormData.text,
        page = noteFormData.page,
        type = noteFormData.type,
        lastModifiedTimestamp = lastModifiedTimestamp.toString(),
    )
}
