package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.shelf.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetBooksForShelfUseCase(private val repository: BooksRepository) {

    operator fun invoke(selectedShelf: Shelf?): Flow<Response<List<Book>>> {

        return if (selectedShelf == null) {
            repository.getAllBooksStream()
        } else {
            repository.getBooksForShelfAsStream(selectedShelf.id)
        }
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load books for shelf: ${selectedShelf?.id}")
            }
    }
}