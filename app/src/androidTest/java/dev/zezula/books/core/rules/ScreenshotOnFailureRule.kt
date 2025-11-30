package dev.zezula.books.core.rules

import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.graphics.writeToTestStorage
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class ScreenshotOnFailureRule(
    private val composeTestRule: ComposeTestRule,
) : TestWatcher() {

    private val tag: String = "ScreenshotOnFailureRule"

    override fun failed(e: Throwable?, description: Description) {
        try {
            val capturedBitmap = composeTestRule
                .onRoot()
                .captureToImage()
                .asAndroidBitmap()
            val fileName = "${description.className}_${description.methodName}"
            capturedBitmap.writeToTestStorage(fileName)

            Log.i(tag, "Saved screenshot for failed test as: $fileName")
        } catch (e: Exception) {
            Log.e(tag, "Failed to capture screenshot on test failure", e)
        }
    }
}
