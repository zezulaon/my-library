package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.reference.Reference
import dev.zezula.books.data.model.reference.ReferenceId
import dev.zezula.books.data.source.network.ApiType
import dev.zezula.books.data.source.network.OnlineBookFinderService
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import kotlinx.coroutines.flow.first
import timber.log.Timber

class UpdateBookReferencesUseCase(
    private val booksRepository: BooksRepository,
    private val onlineBookFinderService: OnlineBookFinderService,
) {

    suspend operator fun invoke(bookId: String): Response<Unit> {
        return asResponse {
            val existingReferences = booksRepository.getReferencesStream(bookId).first()

            val isbn = booksRepository.getBook(bookId)?.isbn
            if (isbn != null) {
                val newReferences = mutableMapOf<String, String?>()
                // Find out if we have a reference for Google API
                newReferences.putAll(getNewGoogleReferences(existingReferences, isbn))

                // Find out if we have a reference for OpenLibrary API
                newReferences.putAll(getNewOpenLibraryReferences(existingReferences, isbn))

                // Find out if we have a reference for Goodreads API
                newReferences.putAll(getNewGoodreadsReferences(existingReferences, isbn))

                val coverReferences = newReferences.filterKeys { id -> id.contains(other = "COVER", ignoreCase = true) }
                updateBookCover(bookId, coverReferences)

                // Removes all cover references from newReferences. We don't need to store them.
                coverReferences.forEach { (id, _) ->
                    newReferences.remove(id)
                }
                newReferences.forEach { (id, value) ->
                    booksRepository.addOrUpdateReference(referenceId = id, bookId = bookId, value = value)
                }

            } else {
                Timber.e("Book with id: [$bookId] has no ISBN -> Reference update skipped.")
            }
        }
            .onError {
                Timber.e(it, "Failed to update references for book: [$bookId].")
            }
    }

    private suspend fun updateBookCover(
        bookId: String,
        newReferences: Map<String, String?>
    ) {
        Timber.d("checkBookCover()")
        val book = booksRepository.getBook(bookId)
        if (book != null && book.thumbnailLink.isNullOrEmpty()) {
            val coverId = newReferences
                .filterValues { it != null }
                .keys.firstOrNull {
                    it == ReferenceId.OL_BOOK_COVER_M.id
                            || it == ReferenceId.GOOGLE_VOLUME_COVER_LINK.id
                            || it == ReferenceId.GOODREADS_BOOK_COVER.id
                }
            val coverUrl = newReferences[coverId]
            Timber.d("New cover url: [$coverUrl] -> Updating book cover.")
            if (coverUrl != null) {
                booksRepository.updateBookCover(bookId = bookId, coverUrl = coverUrl)
            }
        } else {
            Timber.d("Book with id: [$bookId] has cover -> Cover update skipped.")
        }
    }

    private suspend fun getNewGoogleReferences(
        existingReferences: List<Reference>,
        isbn: String,
    ): Map<String, String?> {
        val googleVolumeId =
            existingReferences.firstOrNull { reference -> reference.id == ReferenceId.GOOGLE_VOLUME_ID.id }
        return if (googleVolumeId?.dateUpdated == null) {
            val references = onlineBookFinderService.findReferencesForIsbn(isbn = isbn, apiType = ApiType.GOOGLE)
            references
        } else {
            emptyMap()
        }
    }

    private suspend fun getNewOpenLibraryReferences(
        existingReferences: List<Reference>,
        isbn: String,
    ): Map<String, String?> {
        val olKey = existingReferences.firstOrNull { reference -> reference.id == ReferenceId.OL_BOOK_KEY.id }
        return if (olKey?.dateUpdated == null) {
            val references = onlineBookFinderService.findReferencesForIsbn(isbn = isbn, apiType = ApiType.OPEN_LIBRARY)
            references
        } else {
            emptyMap()
        }
    }

    private suspend fun getNewGoodreadsReferences(
        existingReferences: List<Reference>,
        isbn: String,
    ): Map<String, String?> {
        val goodreadsBookId =
            existingReferences.firstOrNull { reference -> reference.id == ReferenceId.GOODREADS_BOOK_ID.id }
        return if (goodreadsBookId?.dateUpdated == null) {
            val references = onlineBookFinderService.findReferencesForIsbn(isbn = isbn, apiType = ApiType.GOODREADS)
            references
        } else {
            emptyMap()
        }
    }

}
