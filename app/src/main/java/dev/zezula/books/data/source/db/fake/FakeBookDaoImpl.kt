package dev.zezula.books.data.source.db.fake

import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeBookDaoImpl : BookDao {

    private var bookFlow: MutableStateFlow<Map<String, BookEntity>> = MutableStateFlow(emptyMap())
    private var notesFlow: MutableStateFlow<Map<String, List<NoteEntity>>> = MutableStateFlow(emptyMap())

    override fun getAllBooksAsStream(): Flow<List<BookEntity>> = bookFlow.asBooks()

    override fun getBook(bookId: String): Flow<BookEntity?> = bookFlow.map { it[bookId] }

    override fun getNotesForBook(bookId: String): Flow<List<NoteEntity>> {
        return notesFlow.map { it[bookId] ?: emptyList() }
    }

    override suspend fun addOrUpdateNote(note: NoteEntity) {
        TODO("Updating notes is not yet implemented")
    }

    override suspend fun deleteNote(noteId: String) {
        TODO("Deleting notes is not yet implemented")
    }

    override suspend fun getForIsbn(isbn: String): List<BookEntity> =
        bookFlow.first().values.filter { entity -> entity.isbn == isbn }

    override suspend fun getBookCount(): Int = bookFlow.first().size

    override suspend fun addOrUpdate(book: BookEntity) {
        addOrUpdate(listOf(book))
    }

    override suspend fun addOrUpdate(books: List<BookEntity>) {
        bookFlow.update { bookMap ->
            bookMap.toMutableMap().apply {
                putAll(books.associateBy { entity -> entity.id })
            }
        }
    }

    override suspend fun delete(bookId: String) {
        bookFlow.update { bookMap ->
            bookMap.toMutableMap().apply {
                remove(bookId)
            }
        }
    }

    override suspend fun deleteAll() {
        bookFlow.value = emptyMap()
    }

    private fun MutableStateFlow<Map<String, BookEntity>>.asBooks() = map { it.values.toList() }
}
