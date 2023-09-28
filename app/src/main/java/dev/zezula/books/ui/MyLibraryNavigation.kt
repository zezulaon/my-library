package dev.zezula.books.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.navigation.NavHostController
import dev.zezula.books.BuildConfig
import dev.zezula.books.R
import dev.zezula.books.data.model.book.Book
import dev.zezula.books.ui.DestinationArgs.barcodeArg
import dev.zezula.books.ui.DestinationArgs.bookIdArg
import dev.zezula.books.ui.Destinations.bookListRoute
import dev.zezula.books.ui.Destinations.findBookRoute
import dev.zezula.books.ui.Destinations.scanBarcodeRoute
import dev.zezula.books.ui.Destinations.shelvesRoute
import dev.zezula.books.ui.Destinations.signInRoute
import dev.zezula.books.ui.MyLibraryScreens.bookDetail
import dev.zezula.books.ui.MyLibraryScreens.bookForm
import dev.zezula.books.ui.MyLibraryScreens.bookList
import dev.zezula.books.ui.MyLibraryScreens.findBook
import dev.zezula.books.ui.MyLibraryScreens.scanBarcode
import dev.zezula.books.ui.MyLibraryScreens.searchBarcode
import dev.zezula.books.ui.MyLibraryScreens.shelves
import dev.zezula.books.ui.MyLibraryScreens.signIn
import dev.zezula.books.util.findMyLibraryMainActivity
import timber.log.Timber

private object MyLibraryScreens {
    const val signIn = "signIn"
    const val bookList = "bookList"
    const val bookForm = "bookForm"
    const val bookDetail = "bookDetail"
    const val shelves = "shelves"
    const val scanBarcode = "scanBarcode"
    const val findBook = "findBook"
    const val searchBarcode = "searchBarcode"
}

object DestinationArgs {
    const val bookIdArg = "bookId"
    const val barcodeArg = "barcode"
}

object Destinations {
    const val signInRoute = signIn
    const val bookListRoute = bookList
    const val bookFormRoute = "$bookForm/{$bookIdArg}"
    const val bookDetailRoute = "$bookDetail/{$bookIdArg}"
    const val shelvesRoute = shelves
    const val scanBarcodeRoute = scanBarcode
    const val findBookRoute = findBook
    const val searchBarcodeRoute = "$searchBarcode/{$barcodeArg}"
}

fun NavHostController.navigateFromOnboardingToHome() {
    navigateReplaceTo(
        targetDestination = bookListRoute,
        destinationToReplace = signInRoute,
    )
}

fun NavHostController.navigateToScanBarcode() {
    navigate(scanBarcodeRoute)
}

fun NavHostController.navigateToFindBookOnline() {
    navigate(findBookRoute)
}

fun NavHostController.navigateToSearchBarcode(barcode: String) {
    this.navigate("$searchBarcode/$barcode") {
        // popUpTo() ensures that there will be only one search result screen right after book list screen
        popUpTo(bookListRoute) {
            inclusive = false
        }
    }
}

fun NavHostController.navigateToAddOrEdit(bookId: String? = null) {
    navigate("$bookForm/$bookId")
}

fun NavHostController.navigateToBookDetail(bookId: String, popupToBookList: Boolean = true) {
    navigate("$bookDetail/$bookId") {
        if (popupToBookList) {
            // popUpTo() ensures that there will be only one book detail screen right after book list screen
            popUpTo(bookListRoute) {
                inclusive = false
            }
        }
    }
}

fun NavHostController.navigateToManageShelves() {
    navigate(shelvesRoute)
}

fun NavHostController.navigateReplaceTo(targetDestination: String, destinationToReplace: String) {
    Timber.d("navigateTo($targetDestination)")
    this.navigate(targetDestination) {
        popUpTo(destinationToReplace) {
            inclusive = true
        }
    }
}

// External destinations
fun NavHostController.navigateToReviewDetail(reviewLink: String) {
    context.startActivity(
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(reviewLink)
        },
    )
}

fun NavHostController.navigateToBookUrl(url: String) {
    navigateToLink(url)
}

fun NavHostController.navigateToGoogleSignIn() {
    context.findMyLibraryMainActivity().apply {
        beginGoogleSignIn()
    }
}

fun NavHostController.navigateToContactEmailDraft() {
    val to = BuildConfig.ML_CONTACT_EMAIL
    val subjectPrefix = context.getString(R.string.error_email_contact_subject)
    val subjectFull = "$subjectPrefix (${BuildConfig.APPLICATION_ID}:${BuildConfig.VERSION_CODE})"
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // ensures only email apps should handle this
        putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        putExtra(Intent.EXTRA_SUBJECT, subjectFull)
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Timber.w("No email client found")
        Toast.makeText(context, context.getString(R.string.error_message_no_email_client), Toast.LENGTH_SHORT).show()
    }
}

fun NavHostController.navigateToReleaseNotes() {
    val releaseInfoUrl = BuildConfig.ML_URL_RELEASE_INFO
    navigateToLink(releaseInfoUrl)
}

private fun NavHostController.navigateToLink(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        Timber.w("No browser client found")
        Toast.makeText(context, context.getString(R.string.error_message_no_browser), Toast.LENGTH_SHORT).show()
    }
}
