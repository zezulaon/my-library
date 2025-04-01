package dev.zezula.books

import android.app.Application
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dev.zezula.books.di.appModule
import dev.zezula.books.di.flavoredAppModule
import dev.zezula.books.data.BackupService
import dev.zezula.books.util.CrashlyticsTree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MyLibraryApp : Application() {

    private val backUpService: BackupService by inject()

    override fun onCreate() {
        super.onCreate()

        Firebase.crashlytics.setCrashlyticsCollectionEnabled(BuildConfig.DEBUG.not())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        startKoin {
            androidContext(this@MyLibraryApp)
            androidLogger()
            modules(appModule, flavoredAppModule)
        }

        backUpService.startBackupService()
    }
}
