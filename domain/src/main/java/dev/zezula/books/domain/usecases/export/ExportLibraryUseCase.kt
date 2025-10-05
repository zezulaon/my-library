package dev.zezula.books.domain.usecases.export

import dev.zezula.books.core.model.Book
import dev.zezula.books.core.model.NoteWithBook
import dev.zezula.books.core.model.ShelfForBook
import dev.zezula.books.domain.repositories.NotesRepository
import dev.zezula.books.domain.repositories.ShelvesRepository
import dev.zezula.books.domain.repositories.UserLibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import java.io.File
import java.io.IOException

private val CSV_BOOKS_HEADER = listOf(
    "ID",
    "Title",
    "Author",
    "Description",
    "ISBN",
    "Publisher",
    "Year Published",
    "Page Count",
    "Thumbnail Link",
    "User Rating",
    "Date Added",
)

private val CSV_NOTES_HEADER = listOf(
    "ID",
    "Note",
    "Book ID",
    "Book Title",
)

private val CSV_SHELVES_HEADER = listOf(
    "ID",
    "Shelf",
    "Book ID",
    "Book Title",
)

class ExportLibraryUseCase(
    private val userLibraryRepository: UserLibraryRepository,
    private val notesRepository: NotesRepository,
    private val shelvesRepository: ShelvesRepository,
    private val getExportDirUseCase: GetExportDirUseCase,
) {

    suspend operator fun invoke() {
        withContext(context = Dispatchers.IO) {
            val timestamp = createTimestamp()

            exportData(
                fileName = "books",
                csvRows = createBookCsvRows(),
                timestamp = timestamp,
            )

            exportData(
                fileName = "notes",
                csvRows = createNoteCsvRows(),
                timestamp = timestamp,
            )

            exportData(
                fileName = "shelves",
                csvRows = createShelvesCsvRows(),
                timestamp = timestamp,
            )
        }
    }

    private fun createTimestamp(): String {
        val now = Clock.System.now()
        val timestamp = now.toLocalDateTime(timeZone = TimeZone.UTC)
            .toString()
            .replace(":", "-")
        return timestamp
    }

    private suspend fun createBookCsvRows(): List<List<String?>> {
        val books: List<Book> = userLibraryRepository.getAllLibraryBooksFlow().firstOrNull().orEmpty()

        val bookValues = books
            .map { book ->
                listOf(
                    book.id.value,
                    book.title,
                    book.author,
                    book.description,
                    book.isbn,
                    book.publisher,
                    book.yearPublished?.toString(),
                    book.pageCount?.toString(),
                    book.thumbnailLink,
                    book.userRating?.toString(),
                    book.dateAdded,
                )
            }

        return buildList {
            add(CSV_BOOKS_HEADER)
            addAll(bookValues)
        }
    }

    private suspend fun createNoteCsvRows(): List<List<String?>> {
        val notes: List<NoteWithBook> = notesRepository.getAllNotesFlow().firstOrNull().orEmpty()

        val noteValues = notes
            .map { note ->
                listOf(
                    note.note.id.value,
                    note.note.text,
                    note.note.bookId.value,
                    note.bookTitle,
                )
            }

        return buildList {
            add(CSV_NOTES_HEADER)
            addAll(noteValues)
        }
    }

    private suspend fun createShelvesCsvRows(): List<List<String?>> {
        val books: List<Book> = userLibraryRepository
            .getAllLibraryBooksFlow()
            .firstOrNull()
            .orEmpty()

        val pairs: List<Pair<Book, List<ShelfForBook>>> = books
            .map { book ->
                val shelvesForBook: List<ShelfForBook> = shelvesRepository
                    .getAllShelvesForBookFlow(book.id)
                    .firstOrNull()
                    .orEmpty()
                    .filter { it.isBookAdded }

                book to shelvesForBook
            }

        val shelfValues = pairs
            .map { (book, shelves) ->
                shelves.map {
                    listOf(
                        it.id.value,
                        it.title,
                        book.id.value,
                        book.title,
                    )
                }
            }
            .flatten()

        return buildList {
            add(CSV_SHELVES_HEADER)
            addAll(shelfValues)
        }
    }

    private fun exportData(fileName: String, csvRows: List<List<String?>>, timestamp: String) {
        try {
            val exportDir = getExportDirUseCase()
            if (exportDir == null) {
                Timber.e("Export directory is null")
                return
            }
            val file = File(exportDir, "exported_${fileName}_$timestamp.csv")

            file.printWriter().use { writer ->
                csvRows
                    .map { it.joinToString(",", transform = ::escapeCsvField) }
                    .forEach { writer.println(it) }
            }
        } catch (e: IOException) {
            Timber.e(e, "Failed to export library")
        }
    }

    private fun escapeCsvField(value: String?): String {
        if (value == null) return "\"\""
        // Escape double quotes by doubling them
        val escaped = value.replace("\"", "\"\"")
        // Wrap in double quotes
        return "\"$escaped\""
    }
}
