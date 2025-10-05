package dev.zezula.books.di

import dev.zezula.books.data.di.testDataModuleOverride
import org.koin.dsl.module

/**
 * Provides fake (in memory) DB and fake services and data sources. In navigation tests, this module overrides
 * production modules.
 */
val appInstrumentedTestModule = module {
    includes(testDataModuleOverride)
}
