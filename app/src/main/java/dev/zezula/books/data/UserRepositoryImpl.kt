package dev.zezula.books.data

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.zezula.books.data.source.network.COLLECTION_ID_USERS
import dev.zezula.books.data.source.network.FIELD_LAST_SIGNED_IN_DATE
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate

class UserRepositoryImpl : UserRepository {

    override suspend fun updateLastSignedInDate() {
        val lastSignedInDate = LocalDate.now().toString()

        Timber.d("updateLastSignedInDate(lastSignedInDate=$lastSignedInDate)")

        // Skip the update if there is no user created yet
        val userId = Firebase.auth.currentUser?.uid ?: return

        val valuesMap = mapOf(FIELD_LAST_SIGNED_IN_DATE to lastSignedInDate)
        val userDocument = Firebase.firestore.collection(COLLECTION_ID_USERS).document(userId)
        val userDocumentExists = userDocument.get().await().exists()
        if (userDocumentExists) {
            userDocument.update(valuesMap).await()
        } else {
            userDocument.set(valuesMap).await()
        }
    }
}
