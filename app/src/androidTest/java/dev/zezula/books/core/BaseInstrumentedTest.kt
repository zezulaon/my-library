package dev.zezula.books.core

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dev.zezula.books.core.rules.KoinTestRule
import dev.zezula.books.core.rules.PrintSemanticsOnFailureRule
import dev.zezula.books.core.rules.ScreenshotOnFailureRule
import dev.zezula.books.di.appInstrumentedTestModule
import dev.zezula.books.di.appModule
import dev.zezula.books.di.flavoredAppModule
import dev.zezula.books.ui.MyLibraryMainActivity
import org.junit.Rule
import org.koin.test.KoinTest

open class BaseInstrumentedTest : KoinTest {

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule(
        modules = listOf(appModule, flavoredAppModule, appInstrumentedTestModule),
    )

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MyLibraryMainActivity>()

    @get:Rule(order = 2)
    val printSemanticsOnFailureRule = PrintSemanticsOnFailureRule(composeTestRule)

    @get:Rule(order = 3)
    val screenshotOnFailureRule = ScreenshotOnFailureRule(composeTestRule)
}
