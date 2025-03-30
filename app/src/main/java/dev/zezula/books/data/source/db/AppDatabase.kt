package dev.zezula.books.data.source.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.book.BookSuggestionEntity
import dev.zezula.books.data.model.book.SearchBookResultEntity
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.review.RatingEntity
import dev.zezula.books.data.model.review.ReviewEntity
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import timber.log.Timber

@Database(
    entities = [
        BookEntity::class,
        SearchBookResultEntity::class,
        BookSuggestionEntity::class,
        ReviewEntity::class,
        RatingEntity::class,
        ShelfEntity::class,
        ShelfWithBookEntity::class,
        NoteEntity::class,
    ],
    version = 10,
    // https://developer.android.com/training/data-storage/room/migrating-db-versions
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
    ],
)
abstract class AppDatabase : RoomDatabase() {

    override fun init(configuration: DatabaseConfiguration) {
        Timber.d("init()")
        super.init(configuration)
    }

    override fun close() {
        Timber.d("close()")
        super.close()
    }

    abstract fun bookDao(): BookDao
    abstract fun bookSearchResultDao(): SearchBookResultDao
    abstract fun bookSuggestionDao(): BookSuggestionDao
    abstract fun noteDao(): NoteDao
    abstract fun shelfDao(): ShelfDao
    abstract fun shelfAndBookDao(): ShelfAndBookDao
    abstract fun reviewDao(): ReviewDao
    abstract fun ratingDao(): RatingDao
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS library_books (bookId TEXT NOT NULL, PRIMARY KEY(bookId)," +
                " FOREIGN KEY(bookId) REFERENCES books (id) ON UPDATE NO ACTION ON DELETE CASCADE)",
        )
        db.execSQL("INSERT INTO library_books (bookId) SELECT id FROM books")
    }
}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE books ADD COLUMN isInLibrary INTEGER NOT NULL DEFAULT 0")
        db.execSQL("UPDATE books SET isInLibrary = 1 WHERE id IN (SELECT bookId FROM library_books)")
        db.execSQL("DROP TABLE library_books")
    }
}

val MIGRATION_9_10 = object : Migration(9, 10) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE notes SET isPendingSync = 1")

        db.execSQL("UPDATE notes SET lastModifiedTimestamp = \"2025-03-24T12:00:00.000Z\"")
        db.execSQL("UPDATE books SET lastModifiedTimestamp = \"2025-03-24T12:00:00.000Z\"")
        db.execSQL("UPDATE shelves SET lastModifiedTimestamp = \"2025-03-24T12:00:00.000Z\"")
        db.execSQL("UPDATE shelf_with_book SET lastModifiedTimestamp = \"2025-03-24T12:00:00.000Z\"")
    }
}
