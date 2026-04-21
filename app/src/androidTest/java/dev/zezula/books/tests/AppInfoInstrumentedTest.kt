package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onAppInfoScreen
import dev.zezula.books.tests.robot.onHomeScreen
import org.junit.Test

class AppInfoInstrumentedTest : BaseInstrumentedTest() {

    @Test
    fun when_user_clicks_export_then_exported_files_are_displayed() {
        onApp(composeTestRule) {
            onHomeScreen {
                navigateToAppInfo()
            }

            onAppInfoScreen {
                tapOnExport()

                assertExportedFileIsDisplayed("exported_shelves")
                assertExportedFileIsDisplayed("exported_notes")
                assertExportedFileIsDisplayed("exported_books")
            }
        }
    }
}
