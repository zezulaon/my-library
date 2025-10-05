package dev.zezula.books.core.utils

import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.lang.IllegalStateException

fun shortUserId(): String {
    val id = try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.substring(0, 4)
    } catch (e: IllegalStateException) {
        Timber.e(e)
        null
    }
    return id ?: "n/a"
}
