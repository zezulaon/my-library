package dev.zezula.books.data.model.user

import dev.zezula.books.data.source.network.FIELD_LEGACY_APP_ID
import dev.zezula.books.data.source.network.FIELD_LEGACY_DB_MIGRATED
import dev.zezula.books.data.source.network.FIELD_MIGRATION_DURATION_IN_SECONDS
import dev.zezula.books.data.source.network.FIELD_MIGRATION_ERROR
import dev.zezula.books.data.source.network.FIELD_VERSION_CODE

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkMigrationData(
    val legacyAppId: String? = null,
    val legacyDbMigrated: Boolean? = null,
    val migrationError: String? = null,
    val versionCode: String? = null,
    val migrationDurationInSeconds: String? = null,
)

fun NetworkMigrationData.toMapValues(): Map<String, Any?> {
    return mapOf(
        FIELD_LEGACY_APP_ID to legacyAppId,
        FIELD_LEGACY_DB_MIGRATED to legacyDbMigrated,
        FIELD_MIGRATION_ERROR to migrationError,
        FIELD_VERSION_CODE to versionCode,
        FIELD_MIGRATION_DURATION_IN_SECONDS to migrationDurationInSeconds,
    )
}
