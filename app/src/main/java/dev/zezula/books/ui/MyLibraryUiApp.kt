package dev.zezula.books.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import dev.zezula.books.BuildConfig
import dev.zezula.books.ui.theme.MyLibraryTheme
import timber.log.Timber

@Composable
fun MyLibraryUiApp(startDestination: String = Destinations.signInRoute) {
    MyLibraryTheme {

        val navController = rememberNavController()

        if (BuildConfig.DEBUG) {
            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { controller, _, _ ->
                    val route = controller.backQueue.map { it.destination.route }.joinToString(separator = " -> ")
                    Timber.d("NavController destinations changed: $route")
                }
            }
        }

        MyLibraryNavHost(
            startDestination = startDestination,
            navController = navController
        )
    }
}