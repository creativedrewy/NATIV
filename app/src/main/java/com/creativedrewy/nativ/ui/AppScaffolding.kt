package com.creativedrewy.nativ.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender

sealed class AppScreen(
    val route: String
)

object NavArgs {
    const val nftId = "nftId"
    const val collectionId = "collectionId"
}

object Gallery : AppScreen("gallery")
object Wallpapers : AppScreen("wallpapers")
object Settings : AppScreen("settings")
object CollectionDetail : AppScreen("collection/{${NavArgs.collectionId}}")
object Details : AppScreen("details/{${NavArgs.nftId}}")

@ExperimentalComposeUiApi
@Composable
fun AppScreenContent(
    activityResultSender: ActivityResultSender
) {
    val navController = rememberNavController()
    val listState = rememberLazyGridState()

    fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Gallery.route
    ) {
        composable(Gallery.route) {
            TabScreen(
                navDest = it.destination,
                onNavItemClick = { route -> navigate(route) },
                onSettingsClick = { navController.navigate(Settings.route) }
            ) {
                CollectionsScreen(
                    onCollectionNavigate = { collectionId ->
                        navController.navigate("collection/$collectionId")
                    },
                    onNftNavigate = { assetId ->
                        navController.navigate("details/$assetId")
                    },
                    listState = listState
                )
            }
        }
        composable(Wallpapers.route) {
            TabScreen(
                navDest = it.destination,
                onNavItemClick = { route -> navigate(route) },
                onSettingsClick = { navController.navigate(Settings.route) }
            ) {
                SelectWallpaperScreen()
            }
        }
        composable(Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onConnectWallet = {},
                activityResultSender = activityResultSender
            )
        }
        composable(
            route = CollectionDetail.route,
            arguments = listOf(navArgument(NavArgs.collectionId) { type = NavType.StringType })
        ) {
            CollectionDetailScreen(
                collectionId = it.arguments?.getString(NavArgs.collectionId) ?: "",
                onNftNavigate = { assetId ->
                    navController.navigate("details/$assetId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Details.route,
            arguments = listOf(navArgument(NavArgs.nftId) { type = NavType.StringType })
        ) {
            DetailsScreen(it.arguments?.getString(NavArgs.nftId) ?: "")
        }
    }
}

@Composable
fun TabScreen(
    navDest: NavDestination?,
    onNavItemClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    screenContent: @Composable () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(MaterialTheme.colors.primary)
            ) {
                // Centered logo
                Image(
                    modifier = Modifier
                        .width(135.dp)
                        .aspectRatio(1.7f)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(
                        id = R.drawable.nativ_logo
                    ),
                    contentDescription = ""
                )

                // User icon in upper right
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Settings",
                        tint = Turquoise,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.primary
            ) {
                BottomNavigationContents(
                    navDest = navDest,
                    onNavItemClick = onNavItemClick
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colors.primary)
        ) {
            screenContent()
        }
    }
}

@Composable
fun BottomNavigationContents(
    navDest: NavDestination?,
    onNavItemClick: (String) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(
                        id = R.drawable.ic_grid
                    ),
                    contentDescription = "Gallery"
                )
            },
            selectedContentColor = Turquoise,
            unselectedContentColor = Turquoise.copy(0.6f),
            alwaysShowLabel = true,
            label = {
                Text("Gallery")
            },
            selected = navDest?.hierarchy?.any { it.route == Gallery.route } == true,
            onClick = {
                onNavItemClick(Gallery.route)
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(
                        id = R.drawable.ic_phone_wallpaper
                    ),
                    contentDescription = "Wallpapers"
                )
            },
            selectedContentColor = Turquoise,
            unselectedContentColor = Turquoise.copy(0.6f),
            alwaysShowLabel = true,
            label = {
                Text("Wallpapers")
            },
            selected = navDest?.hierarchy?.any { it.route == Wallpapers.route } == true,
            onClick = {
                onNavItemClick(Wallpapers.route)
            }
        )
    }
}
