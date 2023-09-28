package dev.zezula.books.data.model.reference

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.BookEntity

@Entity(
    tableName = "references",
    foreignKeys = [
        ForeignKey(entity = BookEntity::class, parentColumns = ["id"], childColumns = ["bookId"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index(value = ["bookId"]),
    ],
)
data class ReferenceEntity(
    @PrimaryKey
    val id: String,
    val bookId: String,
    // Value of reference, e.g. "1234567890", "https://example.com". (Can be null if the reference doesn't exist)
    val value: String?,
    // Last time the reference was updated.
    val dateUpdated: String,
) {
    fun asExternalModel(): Reference {
        return Reference(
            id = this.id,
            bookId = this.bookId,
            value = this.value,
            dateUpdated = this.dateUpdated,
        )
    }
}

fun fromNetworkReference(
    networkReference: NetworkReference,
    bookId: String,
): ReferenceEntity {
    checkNotNull(networkReference.id) { "Reference needs [id] property" }
    checkNotNull(networkReference.dateUpdated) { "Reference needs [dateUpdated] property" }
    return ReferenceEntity(
        id = networkReference.id,
        bookId = bookId,
        dateUpdated = networkReference.dateUpdated,
        value = networkReference.value,
    )
}