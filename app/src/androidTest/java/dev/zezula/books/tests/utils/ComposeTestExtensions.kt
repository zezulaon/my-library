package dev.zezula.books.tests.utils

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.zezula.books.R

fun AndroidComposeTestRule<*, *>.onNodeWithTextStringRes(@StringRes resId: Int): SemanticsNodeInteraction =
    onNodeWithText(activity.getString(resId))

fun AndroidComposeTestRule<*, *>.tapOnNavigateUp() {
    onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back))
        .performClick()
}
