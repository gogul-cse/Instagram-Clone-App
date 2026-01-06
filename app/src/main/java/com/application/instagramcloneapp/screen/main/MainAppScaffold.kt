package com.application.instagramcloneapp.screen.main

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.application.instagramcloneapp.navigation.InstagramScreen
import com.application.instagramcloneapp.screen.message.MessageScreen
import com.application.instagramcloneapp.screen.profile.ProfileScreen
import com.application.instagramcloneapp.screen.profile.ProfileViewModel
import com.application.instagramcloneapp.screen.search.SearchScreen


@Composable
fun MainAppScaffold(navController: NavController,profileViewModel: ProfileViewModel) {

    val bottomNavController=rememberNavController()
    Scaffold(
        bottomBar = { InstaBottomBar(bottomNavController) },
        containerColor = BackgroundColor
    ) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = InstagramScreen.MainScreen.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(InstagramScreen.MainScreen.name) {
                MainScreen(navController)
            }
            composable(InstagramScreen.SearchScreen.name){
                SearchScreen(navController)
            }
            composable(InstagramScreen.MessageScreen.name) {
                MessageScreen(navController)
            }

            composable(InstagramScreen.ProfileScreen.name) {
                ProfileScreen(navController,profileViewModel)
            }

        }
    }
}

@Preview
// --- BOTTOM NAVIGATION ---
@Composable
fun InstaBottomBar(navController: NavController = NavController(
    LocalContext.current)) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination?.route


    NavigationBar(modifier = Modifier.wrapContentHeight(),
        containerColor = BottomNavColor,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        // Home Icon
        NavigationBarItem(modifier = Modifier.wrapContentHeight(),
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home",
                modifier = Modifier.size(28.dp)) },
            selected = currentRoute == InstagramScreen.MainScreen.name,
            onClick = { navController.navigate(InstagramScreen.MainScreen.name)
            {launchSingleTop = true}
                      },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray)
        )

        // Search Icon
        NavigationBarItem(modifier = Modifier.wrapContentHeight(),
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search",
                modifier = Modifier.size(28.dp)) },
            selected = currentRoute == InstagramScreen.SearchScreen.name,
            onClick = { navController.navigate(InstagramScreen.SearchScreen.name)
            {launchSingleTop = true}
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray)
        )

        NavigationBarItem(modifier = Modifier.wrapContentHeight(),
            icon = { Icon(Icons.AutoMirrored.Default.Message,
                contentDescription = "Messages", modifier = Modifier.size(28.dp)) },
            selected = currentRoute == InstagramScreen.MessageScreen.name,
            onClick = {
                navController.navigate(InstagramScreen.MessageScreen.name)
                {popUpTo(InstagramScreen.MainScreen.name)
                    launchSingleTop = true}
            },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray)
        )

        // Profile Icon
        NavigationBarItem(modifier = Modifier.wrapContentHeight(),
            icon = {
                Icon(Icons.Outlined.Person, contentDescription = "Profile",
                    modifier = Modifier.size(28.dp))
            },
            selected = currentRoute == InstagramScreen.ProfileScreen.name,
            onClick = { navController.navigate(InstagramScreen.ProfileScreen.name)
            {popUpTo(InstagramScreen.MainScreen.name)
                launchSingleTop = true}
                      },
            colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray)
        )
    }
}