package dev.zezula.books.domain.usecases

import dev.zezula.books.core.model.NoteWithBook
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.model.onResponseError
import dev.zezula.books.domain.repositories.NotesRepository
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