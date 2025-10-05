package dev.zezula.books.data.network.api

import dev.zezula.books.core.model.NetworkMigrationData
import dev.zezula.books.data.network.FIELD_LEGACY_APP_ID
import dev.zezula.books.data.network.FIELD_LEGACY_DB_MIGRATED
import dev.zezula.books.data.network.FIELD_MIGRATION_DURATION_IN_SECONDS
import dev.zezula.books.data.network.FIELD_MIGRATION_ERROR
import dev.zezula.books.data.network.FIELD_VERSION_CODE
import dev.zezula.books.data.network.NetworkBook
import dev.zezula.books.data.network.NetworkNote
import dev.zezula.books.data.network.NetworkShelf
import dev.zezula.books.data.network.NetworkShelfWithBook

interface NetworkDataSource {

    suspend fun getMigrationData(): NetworkMigrationData

    suspend fun updateMigrationData(networkMigrationData: NetworkMigrationData)

    suspend fun addOrUpdateBook(book: NetworkBook): NetworkBook

    suspend fun addOrUpdateNote(note: NetworkNote): NetworkNote

    suspend fun addOrUpdateShelf(shelf: NetworkShelf): NetworkShelf

    suspend fun updateBookInShelf(shelfWithBook: NetworkShelfWithBook)

    suspend fun getModifiedShelves(lastModifiedTimestamp: String?): List<NetworkShelf>

    suspend fun getModifiedBooks(lastModifiedTimestamp: String?): List<NetworkBook>

    suspend fun getModifiedShelvesWithBooks(lastModifiedTimestamp: String?): List<NetworkShelfWithBook>

    suspend fun getModifiedNotes(lastModifiedTimestamp: String?): List<NetworkNote>
}

fun NetworkMigrationData.toMapValues(): Map<String, Any?> {
    return mapOf(
        FIELD_LEGACY_APP_ID to legacyAppId,
        FIELD_LEGACY_DB_MIGRATED to legacyDbMigrated,
        FIELD_MIGRATION_ERROR to migrationError,
        FIELD_VERSION_CODE to versionCode,
        FIELD_MIGRATION_DURATION_IN_SECONDS to migrationDurationInSeconds,
    )
}
