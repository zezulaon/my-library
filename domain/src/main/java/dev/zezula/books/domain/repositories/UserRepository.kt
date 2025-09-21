package dev.zezula.books.domain.repositories

interface UserRepository {

    suspend fun updateLastSignedInDate()
}