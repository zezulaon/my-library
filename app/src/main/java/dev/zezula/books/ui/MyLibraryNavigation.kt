package dev.zezula.books.ui

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavHostController
import dev.zezula.books.ui.DestinationArgs.barcodeArg
import dev.zezula.books.ui.DestinationArgs.bookIdArg
import dev.zezula.books.ui.Destinations.bookListRoute
import dev.zezula.books.ui.Destinations.scanBarcodeRoute
import dev.zezula.books.ui.Destinations.shelvesRoute
import dev.zezula.books.ui.Destinations.signInRoute
import dev.zezula.books.ui.MyLibraryScreens.bookDetail
import dev.zezula.books.ui.MyLibraryScreens.bookForm
import dev.zezula.books.ui.MyLibraryScreens.bookList
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
    const val searchBarcodeRoute = "$searchBarcode/{$barcodeArg}"
}

fun NavHostController.navigateFromOnboardingToHome() {
    navigateReplaceTo(
        targetDestination = bookListRoute,
        destinationToReplace = signInRoute
    )
}

fun NavHostController.navigateToScanBarcode() {
    navigate(scanBarcodeRoute)
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

fun NavHostController.navigateToBookDetail(bookId: String) {
    navigate("$bookDetail/$bookId") {
        // popUpTo() ensures that there will be only one book detail screen right after book list screen
        popUpTo(bookListRoute) {
            inclusive = false
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
    context.startActivity(Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(reviewLink)
    })
}

fun NavHostController.navigateToGoogleSignIn() {
    context.findMyLibraryMainActivity().apply {
        beginGoogleSignIn()
    }
}