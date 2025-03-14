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
    version = 7,
    // https://developer.android.com/training/data-storage/room/migrating-db-versions
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
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
    abstract fun noteDao(): NoteDao
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
