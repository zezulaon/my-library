package dev.zezula.books.tests.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.zezula.books.R
import dev.zezula.books.tests.utils.onNodeWithTextStringRes

class SignInRobot(val rule: AndroidComposeTestRule<*, *>) {

    fun tapOnSignInWithEmail() {
        rule.onNodeWithTextStringRes(R.string.btn_email_sign_in).performClick()
    }

    fun typeEmail(email: String) {
        val label = rule.activity.getString(R.string.sign_in_email_label)
        rule.onNodeWithText(label).performTextInput(email)
    }

    fun typePassword(password: String) {
        val label = rule.activity.getString(R.string.sign_in_password_label)
        rule.onNodeWithText(label).performTextInput(password)
    }

    fun tapOnSignIn() {
        rule.onNodeWithTextStringRes(R.string.sign_in_button_text).performClick()
    }
}

fun AppRobot.onSignInScreen(block: SignInRobot.() -> Unit) {
    rule.verifySignInScreenDisplayed()
    SignInRobot(rule).apply(block)
}

private fun AndroidComposeTestRule<*, *>.verifySignInScreenDisplayed() {
    onNodeWithTextStringRes(R.string.btn_google_sign_in).assertIsDisplayed()
}
