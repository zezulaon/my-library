package dev.zezula.books.domain

import dev.zezula.books.data.NotesRepository
import dev.zezula.books.data.model.note.NoteWithBook
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetAllNotesUseCase(private val notesRepository: NotesRepository) {

    /**
     * Returns a list of all authors with the number of their books.
     */
    operator fun invoke(): Flow<Response<List<NoteWithBook>>> {
        return notesRepository.getAllNotesFlow()
            .asResponse()
            .onResponseError {
                Timber.e(it, "Failed to load all notes")
            }
    }
}
