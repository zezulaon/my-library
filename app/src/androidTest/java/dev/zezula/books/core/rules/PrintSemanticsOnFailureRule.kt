package dev.zezula.books.core.rules

import android.util.Log
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class PrintSemanticsOnFailureRule(
    private val composeTestRule: ComposeTestRule,
) : TestWatcher() {

    private val tag: String = "**** Failed Test Semantics"

    override fun failed(e: Throwable?, description: Description) {
        try {
            composeTestRule
                .onRoot(useUnmergedTree = true)
                .printToLog(tag)
        } catch (e: Exception) {
            Log.e(tag, "Failed to print semantics tree", e)
        }
    }
}
