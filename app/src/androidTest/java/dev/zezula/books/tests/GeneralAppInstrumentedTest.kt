package dev.zezula.books.tests

import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import dev.zezula.books.core.BaseInstrumentedTest
import org.junit.Test

class GeneralAppInstrumentedTest : BaseInstrumentedTest() {

    @Test(expected = NoActivityResumedException::class)
    fun given_the_app_is_opened_and_user_navigates_back_then_the_app_quits() {
        composeTestRule.apply {
            Espresso.pressBack()
        }
    }
}
