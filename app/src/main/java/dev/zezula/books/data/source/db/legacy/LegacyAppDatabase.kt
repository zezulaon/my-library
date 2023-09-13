package dev.zezula.books.data.source.db.legacy

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import dev.zezula.books.data.model.legacy.LegacyBookEntity
import dev.zezula.books.data.model.legacy.LegacyGroupShelfBookEntity
import dev.zezula.books.data.model.legacy.LegacyShelfEntity
import timber.log.Timber

@Database(
    entities = [
        LegacyBookEntity::class,
        LegacyShelfEntity::class,
        LegacyGroupShelfBookEntity::class,
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
    ],
)
abstract class LegacyAppDatabase : RoomDatabase() {

    override fun init(configuration: DatabaseConfiguration) {
        Timber.d("init()")
        super.init(configuration)
    }

    override fun close() {
        Timber.d("close()")
        super.close()
    }

    abstract fun legacyBookDao(): LegacyBookDao

    abstract fun legacyShelfDao(): LegacyShelfDao

    abstract fun legacyGroupShelfBookDao(): LegacyGroupShelfBookDao
}
