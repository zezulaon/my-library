package dev.zezula.books.domain

import dev.zezula.books.data.BooksRepository
import dev.zezula.books.data.model.note.Note
import dev.zezula.books.data.model.note.NoteFormData
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class CreateOrUpdateNoteUseCase(private val repository: BooksRepository) {

    suspend operator fun invoke(noteId: String?, noteFormData: NoteFormData, bookId: String): Response<Note> {
        return asResponse {
            repository.addOrUpdateNote(noteId = noteId, bookId = bookId, noteFormData = noteFormData)
        }
            .onError {
                Timber.e(it, "Failed to add the shelf.")
            }
    }
}
