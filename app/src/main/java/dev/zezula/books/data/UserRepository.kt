package dev.zezula.books.data

interface UserRepository {

    suspend fun updateLastSignedInDate()
}
