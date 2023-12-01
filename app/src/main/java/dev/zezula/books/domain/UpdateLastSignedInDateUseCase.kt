package dev.zezula.books.domain

import dev.zezula.books.data.UserRepository
import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import timber.log.Timber

class UpdateLastSignedInDateUseCase(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Response<Unit> {
        return asResponse {
            userRepository.updateLastSignedInDate()
        }
            .onError {
                Timber.e(it, "Failed to update last signed in date.")
            }
    }
}
