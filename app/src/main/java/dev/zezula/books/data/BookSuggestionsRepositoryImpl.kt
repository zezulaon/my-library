package dev.zezula.books.data

import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.asExternalModel
import dev.zezula.books.data.model.book.fromBookFormData
import dev.zezula.books.data.model.myLibrary.toBookFormData
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.network.MyLibraryApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import java.util.UUID

class BookSuggestionsRepositoryImpl(
    private val bookDao: BookDao,
    private val myLibraryApi: MyLibraryApi,
) : BookSuggestionsRepository {

    override suspend fun fetchSuggestions(bookId: String): List<Book>? {
        Timber.d("Fetching suggestions for book: $bookId")
        val parentBook = bookDao.getBookStream(bookId).firstOrNull()
        val title = parentBook?.title
        val author = parentBook?.author
        return if (parentBook != null && title != null && author != null) {
            val suggestions = myLibraryApi.suggestions(title = title, author = author, isbn = parentBook.isbn)
            Timber.d("Fetched suggestions: $suggestions")
            suggestions?.forEach { suggestion ->
                val bookEntity = fromBookFormData(
                    id = UUID.randomUUID().toString(),
                    dateAdded = LocalDateTime.now().toString(),
                    bookFormData = suggestion.toBookFormData(),
                )

                // Check that the book is still in the database (it might have been deleted in the meantime).
                if (bookDao.getBookStream(bookId).firstOrNull() != null) {
                    bookDao.insertBook(bookEntity)
                    bookDao.addToBookSuggestions(BookSuggestionEntity(bookId = bookEntity.id, parentBookId = bookId))
                }
            }
            getAllSuggestionsForBook(bookId).first()
        } else {
            Timber.d("Cannot fetch suggestions for book: $bookId because it is missing some data.")
            null
        }
    }

    override fun getAllSuggestionsForBook(bookId: String): Flow<List<Book>> {
        return bookDao.getSuggestionsForBook(bookId).map {
            it.map(BookEntity::asExternalModel)
        }
    }
}
