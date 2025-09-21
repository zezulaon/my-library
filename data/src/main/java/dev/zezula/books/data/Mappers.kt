package dev.zezula.books.data

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.Note
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.data.database.entities.BookEntity
import dev.zezula.books.data.database.entities.NoteEntity
import dev.zezula.books.data.database.entities.ShelfEntity
import dev.zezula.books.data.database.entities.ShelfWithBookEntity
import dev.zezula.books.data.network.NetworkBook
import dev.zezula.books.data.network.NetworkNote
import dev.zezula.books.data.network.NetworkShelf
import dev.zezula.books.data.network.NetworkShelfWithBook
import kotlinx.datetime.Clock
import timber.log.Timber

fun BookFormData.toBookEntity(id: Book.Id, dateAdded: String, lastModifiedTimestamp: String): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author,
        description = description,
        isbn = isbn,
        publisher = publisher,
        yearPublished = yearPublished,
        pageCount = pageCount,
        userRating = userRating,
        dateAdded = dateAdded,
        subject = subject,
        binding = binding,
        thumbnailLink = thumbnailLink,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

fun BookEntity.asNetworkBook(): NetworkBook {
    // [isInLibrary] is not needed since all [NetworkBook]s are already in library.
    return NetworkBook(
        id = id.value,
        dateAdded = dateAdded,
        title = title,
        author = author,
        description = description,
        subject = subject,
        binding = binding,
        isbn = isbn,
        publisher = publisher,
        yearPublished = yearPublished,
        thumbnailLink = thumbnailLink,
        userRating = userRating,
        pageCount = pageCount,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

fun ShelfEntity.asNetworkShelf(): NetworkShelf {
    return NetworkShelf(
        id = id.value,
        dateAdded = dateAdded,
        title = title,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

fun ShelfWithBookEntity.asNetworkShelfWithBook(): NetworkShelfWithBook {
    return NetworkShelfWithBook(
        bookId = bookId.value,
        shelfId = shelfId.value,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

fun NoteEntity.asNetworkNote(): NetworkNote {
    return NetworkNote(
        id = id.value,
        bookId = bookId.value,
        dateAdded = dateAdded,
        text = text,
        page = page,
        type = type,
        isDeleted = isDeleted,
        lastModifiedTimestamp = lastModifiedTimestamp,
    )
}

fun NetworkBook.asEntity(): BookEntity? {
    val networkBookId: String? = this.id
    val networkBookDateAdded: String? = this.dateAdded
    if (networkBookId == null || networkBookDateAdded == null) {
        Timber.e("ID or dateAdded is null: id=$id, dateAdded=$networkBookDateAdded")
        return null
    } else {
        return BookEntity(
            id = Book.Id(networkBookId),
            dateAdded = networkBookDateAdded,
            title = title,
            author = author,
            description = description,
            isbn = isbn,
            publisher = publisher,
            yearPublished = yearPublished,
            pageCount = pageCount,
            thumbnailLink = thumbnailLink,
            userRating = userRating,
            subject = subject,
            binding = binding,
            isDeleted = isDeleted == true,
            isInLibrary = true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}

fun NetworkNote.asEntity(): NoteEntity? {
    val networkId: String? = this.id
    val networkBookId: String? = this.bookId
    val networkDateAdded: String? = this.dateAdded
    val networkText: String? = this.text
    return if (networkId == null || networkBookId == null || networkDateAdded == null || networkText == null) {
        Timber.e(
            "ID, bookId, dateAdded, or text is null:" +
                    " id=$networkId, bookId=$networkBookId, dateAdded=$networkDateAdded, text=$networkText"
        )
        null
    } else {
        NoteEntity(
            id = Note.Id(networkId),
            bookId = Book.Id(networkBookId),
            dateAdded = networkDateAdded,
            text = networkText,
            page = page,
            type = type,
            isDeleted = isDeleted == true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}

fun NetworkShelf.asEntity(): ShelfEntity? {
    val networkId: String? = this.id
    val networkDateAdded: String? = this.dateAdded
    val networkTitle: String? = this.title
    return if (networkId == null || networkDateAdded == null || networkTitle == null) {
        Timber.e("ID, dateAdded, or title is null: id=$networkId, dateAdded=$networkDateAdded, title=$networkTitle")
        null
    } else {
        ShelfEntity(
            id = Shelf.Id(networkId),
            dateAdded = networkDateAdded,
            title = networkTitle,
            isDeleted = isDeleted == true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}

fun NetworkShelfWithBook.asEntity(): ShelfWithBookEntity? {
    val networkBookId: String? = this.bookId
    val networkShelfId: String? = this.shelfId
    return if (networkBookId == null || networkShelfId == null) {
        Timber.e("Book ID or Shelf ID is null: bookId=$networkBookId, shelfId=$networkShelfId")
        null
    } else {
        ShelfWithBookEntity(
            bookId = Book.Id(networkBookId),
            shelfId = Shelf.Id(networkShelfId),
            isDeleted = isDeleted == true,
            lastModifiedTimestamp = lastModifiedTimestamp ?: Clock.System.now().toString(),
        )
    }
}