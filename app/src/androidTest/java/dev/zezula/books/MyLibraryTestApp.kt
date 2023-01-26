package dev.zezula.books

import android.app.Application
import timber.log.Timber

class MyLibraryTestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")
    }
}
