package dev.zezula.books.legacy

interface MigrationConfigDataProvider {
    fun getApplicationId(): String?
    fun getVersionCode(): String?
}