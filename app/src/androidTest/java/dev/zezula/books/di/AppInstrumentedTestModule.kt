package dev.zezula.books.di

import dev.zezula.books.data.di.testDataModuleOverride
import dev.zezula.books.scanner.IsbnScannerController
import dev.zezula.books.tests.fake.FakeIsbnScannerController
import org.koin.dsl.module

/**
 * Provides fake (in memory) DB and fake services, data sources and other controllers. In navigation tests, this module overrides
 * production modules.
 */
val appInstrumentedTestModule = module {
    includes(testDataModuleOverride)

    single<FakeIsbnScannerController> { FakeIsbnScannerController() }
    single<IsbnScannerController> { get<FakeIsbnScannerController>() }
}
