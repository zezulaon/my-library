package dev.zezula.books.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.previewBookEntities
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import dev.zezula.books.data.model.shelf.previewShelfEntities
import dev.zezula.books.data.source.db.AppDatabase
import dev.zezula.books.data.source.db.BookDao
import dev.zezula.books.data.source.db.ShelfAndBookDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ShelvesTest {

    private lateinit var db: AppDatabase
    private lateinit var bookDao: BookDao
    private lateinit var shelfAndBookDao: ShelfAndBookDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java,
        ).build()
        bookDao = db.bookDao()
        shelfAndBookDao = db.shelfAndBookDao()
    }

    @After
    fun cleanUp() {
        db.close()
    }

    @Test
    fun books_can_be_retrieved_by_shelf_id() = runTest {
        // Insert books
        val booksToAdd = previewBookEntities
        booksToAdd.forEach { entity ->
            bookDao.addOrUpdate(entity)
        }
        // Add another book
        bookDao.addOrUpdate(BookEntity(id = "9877", title = "Another book", dateAdded = "2001"))

        // Insert shelf
        val shelfToAdd = previewShelfEntities.first()
        shelfAndBookDao.addOrUpdate(shelfToAdd)

        // Adds 2 books to the shelf
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[1].id, shelfId = shelfToAdd.id))

        val retrievedBooks = shelfAndBookDao.getBooksForShelfStream(shelfToAdd.id).first()
        // Check that correct books were retrieved
        assertEquals(booksToAdd.sortedByDescending { entity -> entity.dateAdded }, retrievedBooks)

        // Check that wrong shelf id returns nothing
        assertTrue(shelfAndBookDao.getBooksForShelfStream("wrong id").first().isEmpty())
    }

    @Test
    fun book_removal_removes_it_also_from_shelf() = runTest {
        // Insert books
        val booksToAdd = previewBookEntities
        bookDao.addOrUpdate(booksToAdd)

        // Insert shelf
        val shelfToAdd = previewShelfEntities.first()
        shelfAndBookDao.addOrUpdate(shelfToAdd)

        // Adds 2 books to the shelf
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[1].id, shelfId = shelfToAdd.id))

        bookDao.delete(booksToAdd.first().id)

        val retrievedBooks = shelfAndBookDao.getBooksForShelfStream(shelfToAdd.id).first()
        // Check that only 1 book is in the shelf
        assertEquals(1, retrievedBooks.size)
    }

    @Test
    fun book_cannot_be_inserted_into_shelf_more_then_once() = runTest {
        // Insert books
        val booksToAdd = previewBookEntities
        booksToAdd.forEach { entity ->
            bookDao.addOrUpdate(entity)
        }
        // Add another book
        bookDao.addOrUpdate(BookEntity(id = "9877", title = "Another book", dateAdded = "2001"))

        // Insert shelf
        val shelfToAdd = previewShelfEntities.first()
        shelfAndBookDao.addOrUpdate(shelfToAdd)

        // Adds 2 books to the shelf
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))

        // Check that only one book is in the shelf
        val retrievedBooks = shelfAndBookDao.getBooksForShelfStream(shelfToAdd.id).first()
        assertEquals(1, retrievedBooks.size)
    }

    @Test
    fun book_can_be_removed_from_shelf() = runTest {
        // Insert books
        val booksToAdd = previewBookEntities
        booksToAdd.forEach { entity ->
            bookDao.addOrUpdate(entity)
        }
        // Add another book
        bookDao.addOrUpdate(BookEntity(id = "9877", title = "Another book", dateAdded = "2001"))

        // Insert shelf
        val shelfToAdd = previewShelfEntities.first()
        shelfAndBookDao.addOrUpdate(shelfToAdd)

        // Adds 2 books to the shelf
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))
        shelfAndBookDao.removeBookFromShelf(ShelfWithBookEntity(bookId = booksToAdd[0].id, shelfId = shelfToAdd.id))

        // Check that the shelf is empty
        val retrievedBooks = shelfAndBookDao.getBooksForShelfStream(shelfToAdd.id).first()
        assertTrue(retrievedBooks.isEmpty())
    }

    @Test
    fun shelves_are_returned_for_book() = runTest {
        // Insert books
        bookDao.addOrUpdate(previewBookEntities)

        // Insert shelves
        shelfAndBookDao.addOrUpdate(previewShelfEntities)

        // Adds the book to the shelf
        shelfAndBookDao.addBookToShelf(
            ShelfWithBookEntity(
                bookId = previewBookEntities[0].id,
                shelfId = previewShelfEntities[0].id,
            ),
        )

        val retrievedShelves = shelfAndBookDao.getShelvesForBookStream(previewBookEntities[0].id).first()
        // Check that shelves are in DB
        assertEquals(previewShelfEntities.size, retrievedShelves.size)
        // Check that the shelf is associated with the book
        retrievedShelves.any { shelfEntity -> shelfEntity.isBookAdded && shelfEntity.id == previewBookEntities[0].id }
    }

    @Test(expected = SQLiteConstraintException::class)
    fun book_and_shelf_has_to_exist_before_connecting_them_in_junction_table() = runTest {
        shelfAndBookDao.addBookToShelf(ShelfWithBookEntity(bookId = "xxx", shelfId = "yyy"))

        // Check that only one book is in the shelf
        val retrievedBooks = shelfAndBookDao.getBooksForShelfStream("yyy").first()
        assertEquals(1, retrievedBooks.size)
    }

    @Test
    fun shelf_can_be_added_and_retrieved() = runTest {
        // Insert shelves
        shelfAndBookDao.addOrUpdate(previewShelfEntities)

        val retrievedShelves = shelfAndBookDao.getAllShelvesStream().first()
        // Check that shelves are in DB
        assertEquals(
            previewShelfEntities.sortedByDescending { entity -> entity.dateAdded }.map { entity -> entity.id },
            retrievedShelves.map { entity -> entity.id },
        )
    }

    @Test
    fun shelf_can_be_deleted() = runTest {
        // Insert shelves
        shelfAndBookDao.addOrUpdate(previewShelfEntities.first())
        shelfAndBookDao.delete(previewBookEntities.first().id)

        val retrievedShelves = shelfAndBookDao.getAllShelvesStream().first()
        // Check that no shelf is in DB
        assertTrue(retrievedShelves.isEmpty())
    }

    @Test
    fun shelf_can_be_updated() = runTest {
        // Insert shelves
        val shelfToAdd = previewShelfEntities.first()
        val updatedTitle = "updated title"
        shelfAndBookDao.addOrUpdate(shelfToAdd)
        shelfAndBookDao.addOrUpdate(ShelfEntity(id = shelfToAdd.id, dateAdded = "x", title = updatedTitle))

        val retrievedShelves = shelfAndBookDao.getAllShelvesStream().first()
        // Check that shelf is in DB and is updated
        assertTrue(retrievedShelves.any { entity -> entity.title == updatedTitle })
        assertEquals(1, retrievedShelves.size)
    }
}
