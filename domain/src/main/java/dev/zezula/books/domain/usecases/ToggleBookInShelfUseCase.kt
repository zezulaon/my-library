package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.Shelf
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.UserLibraryRepository
import timber.log.Timber

class ToggleBookInShelfUseCase(private val userLibraryRepository: UserLibraryRepository) {

    suspend operator fun invoke(bookId: Book.Id, shelfId: Shelf.Id, isBookInShelf: Boolean): Response<Unit> {
        return asResponse {
            userLibraryRepository.toggleBookInShelf(bookId = bookId, shelfId = shelfId, isBookInShelf = isBookInShelf)
        }
            .onError {
                Timber.e(it, "Failed to update book: [$bookId] in shelf: [$shelfId].")
            }
    }
}