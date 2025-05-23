package dev.zezula.books.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.zezula.books.ui.DestinationArgs.isBulkScanOnArg
import dev.zezula.books.ui.DestinationArgs.shelfIdArg
import dev.zezula.books.ui.screen.appinfo.AppInfoRoute
import dev.zezula.books.ui.screen.authors.AllAuthorsRoute
import dev.zezula.books.ui.screen.authors.AuthorBooksRoute
import dev.zezula.books.ui.screen.create.CreateBookRoute
import dev.zezula.books.ui.screen.detail.BookDetailRoute
import dev.zezula.books.ui.screen.list.BookListRoute
import dev.zezula.books.ui.screen.notes.AllNotesRoute
import dev.zezula.books.ui.screen.search.FindBookRoute
import dev.zezula.books.ui.screen.search.SearchBarcodeRoute
import dev.zezula.books.ui.screen.search.SearchMyLibraryRoute
import dev.zezula.books.ui.screen.shelves.ShelvesRoute
import dev.zezula.books.ui.screen.signin.EmailSignInRoute
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
                onEmailSignIn = { navController.navigateToEmailSignIn() },
                onContactClicked = { navController.navigateToContactEmailDraft() },
                onReleaseNotesClicked = { navController.navigateToReleaseNotes() },
                // Get the same ViewModel as in main activity
                viewModel = koinViewModel(viewModelStoreOwner = LocalContext.current.findMyLibraryMainActivity()),
            )
        }

        composable(route = Destinations.emailSignInRoute) {
            EmailSignInRoute(
                onSignInSuccess = { navController.navigateFromOnboardingToHome() },
                onNavigateBack = { navController.popBackStack() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.bookListRoute) {
            BookListRoute(
                onGoogleSignIn = { navController.navigateToGoogleSignIn() },
                onAddBookManuallyClick = { navController.navigateToAddOrEdit() },
                onScanBookClick = { navController.navigateToBarcodeSearch() },
                onBulkScanBooksClick = { shelfId ->
                    navController.navigateToBarcodeSearch(isBulkScanOn = true, shelfId = shelfId)
                },
                onFindBookOnlineClick = { navController.navigateToFindBookOnline() },
                onBookClick = { bookId -> navController.navigateToBookDetail(bookId) },
                onManageShelvesClick = { navController.navigateToManageShelves() },
                onAllAuthorsShelvesClick = { navController.navigateToAllAuthorsShelves() },
                onAllNotesClick = { navController.navigateToAllNotes() },
                onSearchMyLibraryClick = { navController.navigateToSearchMyLibrary() },
                onMoreClicked = { navController.navigateToAppInfo() },
                viewModel = koinViewModel(),
                // Get the same ViewModel as in main activity
                signInViewModel = koinViewModel(viewModelStoreOwner = LocalContext.current.findMyLibraryMainActivity()),
            )
        }

        composable(route = Destinations.searchMyLibraryRoute) {
            SearchMyLibraryRoute(
                onNavigateBack = { navController.popBackStack() },
                onBookClick = { bookId -> navController.navigateToBookDetail(bookId = bookId) },
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
                onAmazonLinkClicked = { book ->
                    Timber.d("Navigate to Amazon")
                    navController.navigateToAmazonSearch(book)
                },
                onSuggestedBookClick = { bookId -> navController.navigateToBookDetail(bookId = bookId) },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.shelvesRoute) {
            ShelvesRoute(
                onNavigateBack = { navController.popBackStack() },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.allAuthorsRoute) {
            AllAuthorsRoute(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onAuthorClick = { authorNameId ->
                    navController.navigateToAuthorBooks(authorNameId)
                },
            )
        }

        composable(route = Destinations.allNotesRoute) {
            AllNotesRoute(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNoteClick = { bookId -> navController.navigateToBookDetail(bookId = bookId) },
            )
        }

        composable(route = Destinations.authorBookListRoute) {
            AuthorBooksRoute(
                onNavigateBack = { navController.popBackStack() },
                onBookClick = { bookId -> navController.navigateToBookDetail(bookId = bookId) },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.findBookRoute) {
            FindBookRoute(
                onNavigateBack = { navController.popBackStack() },
                onViewBookClick = { bookId -> navController.navigateToBookDetail(bookId = bookId) },
                viewModel = koinViewModel(),
            )
        }

        composable(
            route = Destinations.searchBarcodeRoute,
            arguments = listOf(
                navArgument(isBulkScanOnArg) {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(shelfIdArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            val isBulkScanOn = backStackEntry.arguments?.getBoolean(isBulkScanOnArg) ?: false
            SearchBarcodeRoute(
                isBulkScanningEnabled = isBulkScanOn,
                onNavigateBack = { navController.popBackStack() },
                onBookFound = { bookId -> navController.navigateToBookDetail(bookId = bookId, popupToBookList = true) },
                viewModel = koinViewModel(),
            )
        }

        composable(route = Destinations.appInfoRoute) {
            AppInfoRoute(
                viewModel = koinViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onContactUsClicked = { navController.navigateToContactEmailDraft() },
                onReleaseNotesClicked = { navController.navigateToReleaseNotes() },
            )
        }
    }
}
