package dev.zezula.books.tests.robot

import androidx.compose.ui.test.junit4.AndroidComposeTestRule

class AppRobot(val rule: AndroidComposeTestRule<*, *>)

fun onApp(rule: AndroidComposeTestRule<*, *>, block: AppRobot.() -> Unit) {
    AppRobot(rule).block()
}
