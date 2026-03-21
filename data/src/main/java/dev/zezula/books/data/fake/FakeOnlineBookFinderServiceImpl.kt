package dev.zezula.books.data.fake

import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.core.model.test.testBooksData
import dev.zezula.books.domain.services.OnlineBookFinderService

class FakeOnlineBookFinderServiceImpl : OnlineBookFinderService {

    override suspend fun findBookForIsbnOnline(isbn: String): BookFormData? {
        val scannedBook = testBooksData.firstOrNull {
            it.isbn == isbn
        }

        return if (scannedBook != null) {
            with(scannedBook) {
                BookFormData(
                    title = title,
                    author = author,
                    description = description,
                    isbn = isbn,
                    publisher = publisher,
                    yearPublished = yearPublished,
                    pageCount = pageCount,
                )
            }
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
