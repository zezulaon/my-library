package dev.zezula.books.data.model.legacy

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import kotlinx.datetime.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

// Schema:
// SqliteColumn(name=_id, affinity=INTEGER, isNullable=true, inPrimaryKey=true)
// SqliteColumn(name=isbn13, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=isbn10, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=title, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=binding, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=description, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=numberOfPages, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=publisher, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=publicationDate, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=reviewsFetchedDate, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=offersFetchedDate, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=grRating, affinity=REAL, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=grRatingsCount, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=subject, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=created, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=stateId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=userRating, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=authors, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=lentToName, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=lentToUri, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=familyName, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=thumbnailSmall, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=thumbnailLarge, affinity=TEXT, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=amazonBookId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=grBookId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)
// SqliteColumn(name=googleBookId, affinity=INTEGER, isNullable=true, inPrimaryKey=false)

@Entity(tableName = "books")
data class LegacyBookEntity(
    @PrimaryKey(autoGenerate = true) val _id: Int?,
    val isbn13: String?,
    val isbn10: String?,
    val title: String?,
    val binding: String?,
    val description: String?,
    val numberOfPages: String?,
    val publisher: String?,
    val publicationDate: Long?,
    val reviewsFetchedDate: Long?,
    val offersFetchedDate: Long?,
    val grRating: Double?,
    val grRatingsCount: Int?,
    val subject: String?,
    val created: Long?,
    val stateId: Int?,
    val userRating: Int?,
    val authors: String?,
    val lentToName: String?,
    val lentToUri: String?,
    val familyName: String?,
    val thumbnailSmall: String?,
    val thumbnailLarge: String?,
    val amazonBookId: Int?,
    val grBookId: Int?,
    val googleBookId: Int?,
)

fun LegacyBookEntity.toBookEntity(bookId: String): BookEntity {
    val yearPublished = if (publicationDate != null) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = publicationDate
        cal.get(Calendar.YEAR)
    } else {
        null
    }
    val createdDate = if (created != null) {
        LocalDateTime.ofEpochSecond(created / 1000, 0, ZoneOffset.UTC)
    } else {
        null
    }

    return BookEntity(
        id = Book.Id(bookId),
        title = title,
        author = authors,
        description = description,
        isbn = isbn13 ?: isbn10,
        publisher = publisher,
        yearPublished = yearPublished,
        pageCount = numberOfPages?.toIntOrNull(),
        thumbnailLink = null,
        dateAdded = createdDate?.toString() ?: LocalDateTime.now().toString(),
        userRating = userRating,
        subject = subject,
        binding = binding,
        isInLibrary = true,
        isPendingSync = true,
        lastModifiedTimestamp = Clock.System.now().toString(),
    )
}
