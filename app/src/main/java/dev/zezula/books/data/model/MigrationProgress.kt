package dev.zezula.books.data.model

data class MigrationProgress(
    val type: MigrationType,
    val total: Int,
    val current: Int,
)

enum class MigrationType {
    SHELVES,
    BOOKS,
    GROUPING,
    COMMENTS,
}
