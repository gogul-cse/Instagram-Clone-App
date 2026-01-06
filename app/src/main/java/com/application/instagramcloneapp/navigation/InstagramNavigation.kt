package com.application.instagramcloneapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.application.instagramcloneapp.screen.login.ForgetPasswordScreen
import com.application.instagramcloneapp.screen.login.LoginScreen
import com.application.instagramcloneapp.screen.main.MainAppScaffold
import com.application.instagramcloneapp.screen.message.ChatScreen
import com.application.instagramcloneapp.screen.profile.EditProfileScreen
import com.application.instagramcloneapp.screen.profile.FollowersAndFollowingScreen
import com.application.instagramcloneapp.screen.profile.NewPostScreen
import com.application.instagramcloneapp.screen.profile.PostViewScreen
import com.application.instagramcloneapp.screen.profile.ProfileViewModel
import com.application.instagramcloneapp.screen.search.OtherUserProfileScreen
import com.application.instagramcloneapp.screen.settings.SettingsScreen
import com.application.instagramcloneapp.screen.singup.AddProfileAndBioScreen
import com.application.instagramcloneapp.screen.singup.AddUserIdScreen
import com.application.instagramcloneapp.screen.singup.SignUpScreen
import com.application.instagramcloneapp.screen.singup.SignUpViewModel
import com.application.instagramcloneapp.screen.splash.SplashScreen

@Composable
fun InstagramNavigation() {
    val profileViewModel: ProfileViewModel = hiltViewModel()

    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = InstagramScreen.SplashScreen.name) {
        composable(InstagramScreen.SplashScreen.name){
            SplashScreen(navController = navController)
        }
        composable(InstagramScreen.LoginScreen.name){
            LoginScreen(navController = navController)
        }
        composable(InstagramScreen.ForgetPasswordScreen.name){
            ForgetPasswordScreen(navController = navController)
        }
        navigation(
            route = "signup_graph",
            startDestination = InstagramScreen.SignUpScreen.name
        ) {

            composable(InstagramScreen.SignUpScreen.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("signup_graph")
                }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)

                SignUpScreen(navController, viewModel)
            }

            composable(InstagramScreen.AddUserIdScreen.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("signup_graph")
                }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)

                AddUserIdScreen(navController, viewModel)
            }

            composable(InstagramScreen.AddProfileAndBioScreen.name) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("signup_graph")
                }
                val viewModel: SignUpViewModel = hiltViewModel(parentEntry)

                AddProfileAndBioScreen(navController, viewModel)
            }
        }
        composable(InstagramScreen.MainPage.name){
            MainAppScaffold(navController,profileViewModel)
        }
        composable(InstagramScreen.NewPostScreen.name){
            NewPostScreen(navController = navController,profileViewModel)
        }
        composable(InstagramScreen.PostViewScreen.name){
            PostViewScreen(navController)
        }
        composable(InstagramScreen.EditProfileScreen.name){
            EditProfileScreen(navController)
        }
        composable("${InstagramScreen.ChatScreen.name}/{chatId}",arguments = listOf(
            navArgument("chatId") { type = NavType.StringType }
        )){
            backStackEntry ->
            ChatScreen(navController = navController,
                chatId = backStackEntry.arguments!!.getString("chatId")!!)
        }
        composable(InstagramScreen.SettingsScreen.name){
            SettingsScreen(navController = navController)
        }
        composable("${InstagramScreen.OthersProfileScreen.name}/{user.id}"){
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("user.id")!!
            OtherUserProfileScreen(
                navController = navController,
                userId = userId
            )
        }
        composable(
            route = InstagramScreen.FollowerAndFollowingScreen.name + "?tab={tab}",
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0

            FollowersAndFollowingScreen(
                navController = navController,
                profileViewModel = profileViewModel,
                initialTab = tab
            )
        }



    }

}