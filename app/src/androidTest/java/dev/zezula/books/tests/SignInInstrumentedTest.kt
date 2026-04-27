package dev.zezula.books.tests

import dev.zezula.books.core.BaseInstrumentedTest
import dev.zezula.books.core.fake.FakeAuthServiceImpl
import dev.zezula.books.core.model.BookFormData
import dev.zezula.books.domain.usecases.AddOrUpdateLibraryBookUseCase
import dev.zezula.books.tests.robot.HomeCategory
import dev.zezula.books.tests.robot.onApp
import dev.zezula.books.tests.robot.onHomeScreen
import dev.zezula.books.tests.robot.onSignInScreen
import dev.zezula.books.tests.utils.testBooksData
import kotlinx.coroutines.runBlocking
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.koin.test.inject

class SignInInstrumentedTest : BaseInstrumentedTest() {

    private val addOrUpdateLibraryBookUseCase: AddOrUpdateLibraryBookUseCase by inject()

    companion object {
        @JvmStatic
        @BeforeClass
        fun setupClass() {
            FakeAuthServiceImpl.isUserSignedInInitial = false
        }

        @JvmStatic
        @AfterClass
        fun teardownClass() {
            FakeAuthServiceImpl.isUserSignedInInitial = true
        }
    }

    @Before
    fun setup() = runBlocking {
        testBooksData.forEach {
            addOrUpdateLibraryBookUseCase(
                bookId = null,
                bookFormData = BookFormData(
                    title = it.title,
                    author = it.author,
                    description = it.description,
                    isbn = it.isbn,
                    publisher = it.publisher,
                    yearPublished = it.yearPublished,
                    pageCount = it.pageCount,
                ),
            )
        }
    }

    @Test
    fun when_user_signs_in_with_email_then_home_screen_is_displayed() {
        onApp(composeTestRule) {
            onSignInScreen {
                tapOnSignInWithEmail()
                typeEmail("test@example.com")
                typePassword("password123")
                tapOnSignIn()
            }

            onHomeScreen {
                assertCategoryDisplayed(HomeCategory.AllBooks)
                assertToolbarBookSize(testBooksData.size)
                assertBookTitleIsDisplayed(testBooksData.first().title)
            }
        }
    }
}
