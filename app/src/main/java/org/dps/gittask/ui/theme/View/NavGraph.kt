package org.dps.gittask.ui.theme.View

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object UserInput : Screen("user_input")

    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }

    object RepositoryDetail : Screen("repo_detail/{userId}/{repoName}") {
        fun createRoute(userId: String, repoName: String) = "repo_detail/$userId/$repoName"
    }
}

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.UserInput.route, modifier = modifier) {
        composable(Screen.UserInput.route) {
            UserInputScreen(onUserSubmit = { userId ->
                navController.navigate(Screen.UserProfile.createRoute(userId))
            })
        }
        composable(
            route = Screen.UserProfile.route,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserProfileScreen(userId = userId, onRepoClick = { repoName ->
                navController.navigate(Screen.RepositoryDetail.createRoute(userId, repoName))
            })
        }
        composable(
            route = Screen.RepositoryDetail.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("repoName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val repoName = backStackEntry.arguments?.getString("repoName") ?: return@composable
            RepositoryDetailScreen(userId = userId, repoName = repoName)
        }
    }
}