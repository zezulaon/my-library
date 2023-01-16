package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class ToggleBookInShelfUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(bookId: String, shelfId: String, isBookInShelf: Boolean): Response<Unit> {
        return asResponse {
            repository.updateBookInShelf(bookId = bookId, shelfId = shelfId, isBookInShelf = isBookInShelf)
        }
            .onError {
                Timber.e(it, "Failed to update book: [$bookId] in shelf: [$shelfId].")
            }
    }
}