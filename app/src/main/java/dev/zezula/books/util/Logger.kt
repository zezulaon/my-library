package dev.zezula.books.util

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority >= Log.ERROR
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        with(Firebase.crashlytics) {
            log(message)
            t?.let { recordException(it) }
        }
    }
}