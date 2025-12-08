package dev.zezula.books.tests

import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText

fun AndroidComposeTestRule<*, *>.onNodeWithStringRes(@StringRes resId: Int): SemanticsNodeInteraction =
    onNodeWithText(activity.getString(resId))
