package dev.zezula.books.core.rules

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import timber.log.Timber

class KoinTestRule(
    private val modules: List<Module>,
) : TestWatcher() {

    override fun starting(description: Description) {
        Timber.d("starting()")
        if (GlobalContext.getKoinApplicationOrNull() == null) {
            startKoin {
                androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
                modules(modules)
            }
        } else {
            loadKoinModules(modules)
        }
    }

    override fun finished(description: Description) {
        Timber.d("finished()")
        unloadKoinModules(modules)
    }
}
