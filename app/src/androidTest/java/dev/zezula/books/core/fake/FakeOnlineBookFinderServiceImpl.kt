package dev.zezula.books.core.fake

import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.services.OnlineBookFinderService
import dev.zezula.books.tests.utils.testBooksData

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        val scannedBook = testBooksData.firstOrNull {
            it.isbn == isbn
        }

        return if (scannedBook != null) {
            BookFormData(
                title = scannedBook.title,
                author = scannedBook.author,
                description = scannedBook.description,
                isbn = scannedBook.isbn,
                publisher = scannedBook.publisher,
                yearPublished = scannedBook.yearPublished,
                pageCount = scannedBook.pageCount,
            )
        } else {
            null
        }
    }

    override suspend fun findBookForQueryOnline(query: String): List<BookFormData> {
        return emptyList()
    }

    override suspend fun findBookCoverLinkForIsbn(isbn: String): String? {
        return null
    }
}
