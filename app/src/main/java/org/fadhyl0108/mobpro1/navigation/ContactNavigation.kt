package org.fadhyl0108.mobpro1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.fadhyl0108.mobpro1.ui.screen.ContactFormScreen
import org.fadhyl0108.mobpro1.ui.screen.RecycleBinScreen
import org.fadhyl0108.mobpro1.ui.screen.ContactListScreen

sealed class Screen(val route: String) {
    object ContactList : Screen("contactList")
    object ContactForm : Screen("contactForm/{contactId}") {
        fun createRoute(contactId: Long? = null) = "contactForm/${contactId ?: "new"}"
    }
    object RecycleBin : Screen("recycleBin")
}

@Composable
fun ContactNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ContactList.route
    ) {
        composable(Screen.ContactList.route) {
            ContactListScreen(
                onContactClick = { contactId ->
                    navController.navigate(Screen.ContactForm.createRoute(contactId))
                },
                onAddContact = {
                    navController.navigate(Screen.ContactForm.createRoute())
                },
                onRecycleBinClick = {
                    navController.navigate(Screen.RecycleBin.route)
                }
            )
        }

        composable(
            route = Screen.ContactForm.route,
            arguments = listOf(
                navArgument("contactId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")
            ContactFormScreen(
                contactId = if (contactId == "new") null else contactId?.toLongOrNull(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RecycleBin.route) {
            RecycleBinScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
} 