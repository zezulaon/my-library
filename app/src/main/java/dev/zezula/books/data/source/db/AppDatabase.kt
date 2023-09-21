package dev.zezula.books.data.source.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import dev.zezula.books.data.model.book.BookEntity
import dev.zezula.books.data.model.note.NoteEntity
import dev.zezula.books.data.model.review.RatingEntity
import dev.zezula.books.data.model.review.ReviewEntity
import dev.zezula.books.data.model.shelf.ShelfEntity
import dev.zezula.books.data.model.shelf.ShelfWithBookEntity
import timber.log.Timber

@Database(
    entities = [
        BookEntity::class,
        ReviewEntity::class,
        RatingEntity::class,
        ShelfEntity::class,
        ShelfWithBookEntity::class,
        NoteEntity::class,
    ],
    version = 3,
    // https://developer.android.com/training/data-storage/room/migrating-db-versions
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
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
    abstract fun shelfAndBookDao(): ShelfAndBookDao
    abstract fun reviewDao(): ReviewDao
    abstract fun ratingDao(): RatingDao
}
