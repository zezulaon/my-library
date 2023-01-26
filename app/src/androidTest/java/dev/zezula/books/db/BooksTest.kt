package dev.zezula.books.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.model.book.previewBooks
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.db.BookDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BooksTest {

    private lateinit var db: AppDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()
        bookDao = db.bookDao()
    }

    @After
    fun cleanUp() {
        db.close()
    }

    @Test
    fun bookDao_inserts_and_fetches_items_by_descending_dateAdded() = runTest {
        val booksToAdd = previewBookEntities
        bookDao.addOrUpdate(booksToAdd)

        val storedBooks = bookDao.getAllBooksAsStream().first()
        assertFalse(storedBooks.isEmpty())

        // Check that books are returned sorted by date added descending
        assertEquals(storedBooks, booksToAdd.sortedByDescending { entity -> entity.dateAdded })
        // Check that comparing to collection sorted by ascending fails
        assertNotEquals(storedBooks, booksToAdd.sortedBy { entity -> entity.dateAdded })
    }

    @Test
    fun bookDao_inserts_and_then_get_retrieves_same_entity() = runTest {
        val bookToAdd = previewBookEntities.first()
        bookDao.addOrUpdate(bookToAdd)

        val storedBook = bookDao.getBook(bookToAdd.id).first()
        // Checked that we got the previously inserted book from DB
        assertNotNull(storedBook)
        assertEquals(bookToAdd, storedBook)
    }

    @Test
    fun bookDao_get_with_wrong_id_returns_null() = runTest {
        val storedBook = bookDao.getBook("wrong_id").first()
        assertNull(storedBook)
    }

    @Test
    fun bookDao_get_all_returns_correct_count() = runTest {
        bookDao.addOrUpdate(previewBookEntities)
        val storedBooks = bookDao.getAllBooksAsStream().first()
        assertEquals(previewBooks.size, storedBooks.size)
    }

    @Test
    fun bookDao_delete_removes_books() = runTest {
        bookDao.addOrUpdate(previewBookEntities)

        val booksSizeBeforeDelete = bookDao.getAllBooksAsStream().first().size
        assertEquals(previewBookEntities.size, booksSizeBeforeDelete)

        val bookToDelete = previewBookEntities.first()
        bookDao.delete(bookToDelete.id)
        // There is one less book in the DB
        assertEquals(booksSizeBeforeDelete - 1, bookDao.getAllBooksAsStream().first().size)
        // Deleted book is not in the DB anymore
        assertNull(bookDao.getBook(bookToDelete.id).first())

        bookDao.deleteAll()
        // Check all books were deleted
        assertEquals(0, bookDao.getAllBooksAsStream().first().size)
    }

    @Test
    fun bookDao_updates_upserts_new_book_data() = runTest {
        val bookToAdd = previewBookEntities.first()
        bookDao.addOrUpdate(bookToAdd)

        val updatedTitle = "Updated title"
        val updatedBook = BookEntity(id = bookToAdd.id, title = updatedTitle, dateAdded = "2003")
        bookDao.addOrUpdate(updatedBook)
        // Check that after upsert there is only one item in DB
        assertEquals(1, bookDao.getAllBooksAsStream().first().size)

        val storedBook = bookDao.getBook(bookToAdd.id).first()
        // Check that updated book exists
        assertNotNull(storedBook)
        // Updated book isn't same as previous one
        assertNotEquals(bookToAdd, storedBook)
        // Title was updated
        assertEquals(updatedTitle, storedBook.title)
    }

    @Test
    fun bookDao_existing_isbn_returns_book() = runTest {
        val bookToAdd = previewBookEntities.first()
        bookDao.addOrUpdate(bookToAdd)

        val booksByIsbn = bookDao.getForIsbn(bookToAdd.isbn!!)
        // Check that we can get book by its ISBN
        assertTrue(bookToAdd in booksByIsbn)

        // Check that for wrong ISBN nothing is returned
        val noBooksByIsbn = bookDao.getForIsbn("wrong isbn")
        assertTrue(noBooksByIsbn.isEmpty())
    }
}
