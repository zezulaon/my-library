package dev.zezula.books.tests.robot

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import dev.zezula.books.R

interface BackRobotFeature {
    fun AndroidComposeTestRule<*, *>.tapOnNavigateUp()
}

class BackRobotFeatureImpl() : BackRobotFeature {

    override fun AndroidComposeTestRule<*, *>.tapOnNavigateUp() {
        onNodeWithContentDescription(activity.getString(R.string.content_desc_navigate_back))
            .performClick()
    }
}
