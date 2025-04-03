package dev.zezula.books.domain

import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class CreateNoteUseCase(private val notesRepository: NotesRepository) {

    suspend operator fun invoke(bookId: Book.Id, noteFormData: NoteFormData): Response<Unit> {
        return asResponse {
            notesRepository.createNote(bookId = bookId, noteFormData = noteFormData)
        }
            .onError {
                Timber.e(it, "Failed to add the shelf.")
            }
    }
}
