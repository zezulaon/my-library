package dev.zezula.books.core.model

// Null default values are required when deserializing from firestore [DataSnapshot]. See:
// https://firebase.google.com/docs/database/android/read-and-write#basic_write
data class NetworkMigrationData(
    val legacyAppId: String? = null,
    val legacyDbMigrated: Boolean? = null,
    val migrationError: String? = null,
    val versionCode: String? = null,
    val migrationDurationInSeconds: String? = null,
)

