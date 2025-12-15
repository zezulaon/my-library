package dev.zezula.books.core

import android.app.Application
import timber.log.Timber

class MyLibraryTestApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.Forest.d("onCreate()")
    }
}
