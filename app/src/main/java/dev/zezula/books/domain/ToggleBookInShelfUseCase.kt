package dev.zezula.books.domain

import dev.zezula.books.data.UserLibraryRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class ToggleBookInShelfUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(bookId: Book.Id, shelfId: String, isBookInShelf: Boolean): Response<Unit> {
        return asResponse {
            userLibraryRepository.toggleBookInShelf(bookId = bookId, shelfId = shelfId, isBookInShelf = isBookInShelf)
        }
            .onError {
                Timber.e(it, "Failed to update book: [$bookId] in shelf: [$shelfId].")
            }
    }
}
