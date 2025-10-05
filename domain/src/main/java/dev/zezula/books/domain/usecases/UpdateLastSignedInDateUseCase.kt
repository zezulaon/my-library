package dev.zezula.books.domain.usecases

import dev.zezula.books.domain.model.Response
import dev.zezula.books.domain.model.asResponse
import dev.zezula.books.domain.repositories.UserRepository
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
