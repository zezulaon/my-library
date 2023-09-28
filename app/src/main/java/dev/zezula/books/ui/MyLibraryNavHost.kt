package dev.zezula.books.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.zezula.books.ui.screen.create.CreateBookRoute
import dev.zezula.books.ui.screen.detail.BookDetailRoute
import dev.zezula.books.ui.screen.list.BookListRoute
import dev.zezula.books.ui.screen.scanner.ScanBarcodeRoute
import dev.zezula.books.ui.screen.search.FindBookRoute
import dev.zezula.books.ui.screen.search.SearchBarcodeRoute
import dev.zezula.books.ui.screen.shelves.ShelvesRoute
import dev.zezula.books.ui.screen.signin.SignInRoute
import dev.zezula.books.util.findMyLibraryMainActivity
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun MyLibraryNavHost(
    modifier: Modifier = Modifier,
    startDestination: String,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(route = Destinations.signInRoute) {
            SignInRoute(
                onSignInSuccess = { navController.navigateFromOnboardingToHome() },
                onGoogleSignIn = { navController.navigateToGoogleSignIn() },
                // Get same ViewModel as in main activity
                onContactClicked = { navController.navigateToContactEmailDraft() },
                onReleaseNotesClicked = { navController.navigateToReleaseNotes() },
                viewModel = koinViewModel(viewModelStoreOwner = LocalContext.current.findMyLibraryMainActivity()),
            )
        }

        composable(route = Destinations.bookListRoute) {
            BookListRoute(
                onAddBookManuallyClick = { navController.navigateToAddOrEdit() },
                onScanBookClick = { navController.navigateToScanBarcode() },
                onFindBookOnlineClick = { navController.navigateToFindBookOnline() },
                onBookClick = { bookId -> navController.navigateToBookDetail(bookId) },
                onManageShelvesClick = { navController.navigateToManageShelves() },
                onContactClicked = { navController.navigateToContactEmailDraft() },
                onReleaseNotesClicked = { navController.navigateToReleaseNotes() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.bookFormRoute) {
            CreateBookRoute(
                onNavigateBack = { navController.popBackStack() },
                onItemSavedSuccess = { navController.popBackStack() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.bookDetailRoute) {
            BookDetailRoute(
                onNavigateBack = { navController.popBackStack() },
                onBookDeletedSuccess = { navController.popBackStack() },
                onReviewClick = { review ->
                    Timber.d("Navigate to review: ${review.link}")
                    review.link?.let {
                        navController.navigateToReviewDetail(review.link)
                    }
                },
                onEditBookClick = { bookId ->
                    navController.navigateToAddOrEdit(bookId)
                },
                onNewShelfClick = { navController.navigateToManageShelves() },
                onLinkClicked = { url ->
                    Timber.d("Navigate to: $url")
                    navController.navigateToBookUrl(url)
                },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.shelvesRoute) {
            ShelvesRoute(
                onNavigateBack = { navController.popBackStack() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.findBookRoute) {
            FindBookRoute(
                onNavigateBack = { navController.popBackStack() },
                onViewBookClick = { bookId ->
                    navController.navigateToBookDetail(bookId = bookId, popupToBookList = false)
                },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.searchBarcodeRoute) {
            SearchBarcodeRoute(
                onNavigateBack = { navController.popBackStack() },
                onBookFound = { bookId ->
                    navController.navigateToBookDetail(bookId)
                },
                onScanAgainClick = { navController.navigateToScanBarcode() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.scanBarcodeRoute) {
            ScanBarcodeRoute(
                onNavigateBack = { navController.popBackStack() },
                onBarcodeScanned = { barcode ->
                    navController.navigateToSearchBarcode(barcode)
                },
            )
        }
    }
}
