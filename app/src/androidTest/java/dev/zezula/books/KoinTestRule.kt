package dev.zezula.books

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import timber.log.Timber

class KoinTestRule(
    private val modules: List<Module>,
) : TestWatcher() {

    override fun starting(description: Description) {
        Timber.d("starting()")
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(modules)
        }
    }

    override fun finished(description: Description) {
        Timber.d("finished()")
        stopKoin()
    }
}
