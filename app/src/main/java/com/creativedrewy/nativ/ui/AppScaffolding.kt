package com.creativedrewy.nativ.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomDrawer
import androidx.compose.material.BottomDrawerState
import androidx.compose.material.BottomDrawerValue
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberBottomDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Turquoise
import kotlinx.coroutines.launch

sealed class AppScreen(
    val route: String
)

object NavArgs {
    const val nftId = "nftId"
}

object Gallery : AppScreen("gallery")
object Accounts : AppScreen("accounts")
object Details : AppScreen("details/{${NavArgs.nftId}}")

@ExperimentalComposeUiApi
@Composable
fun AppScreenContent() {
    val navController = rememberNavController()
    val listState = rememberLazyListState()

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
            FabScreens(
                navDest = it.destination,
                showFab = false,
                onNavItemClick = { route -> navigate(route) }
            ) {
                GalleryList(
                    onDetailsNavigate = { id ->
                        navController.navigate("details/$id")
                    },
                    listState = listState
                )
            }
        }
        composable(Accounts.route) {
            FabScreens(
                navDest = it.destination,
                showFab = true,
                onNavItemClick = { route -> navigate(route) }
            ) {
                AddressListScreen()
            }
        }
        composable(
            route = Details.route,
            arguments = listOf(navArgument(NavArgs.nftId) { type = NavType.StringType })
        ) {
            DetailsScreen(it.arguments?.getString(NavArgs.nftId) ?: "")
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun FabScreens(
    navDest: NavDestination?,
    showFab: Boolean,
    onNavItemClick: (String) -> Unit,
    screeContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    BackHandler(
        enabled = drawerState.isExpanded
    ) {
        scope.launch {
            drawerState.close()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colors.primary),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    modifier = Modifier
                        .width(135.dp)
                        .aspectRatio(1.7f),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(
                        id = R.drawable.nativ_logo
                    ),
                    contentDescription = ""
                )
            }
        },
        floatingActionButton = {
            if (showFab) {
                MainAppFab {
                    scope.launch {
                        drawerState.expand()
                    }
                }
            }
        },
        isFloatingActionButtonDocked = showFab,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                cutoutShape = RoundedDiamondFabShape(8.dp),
                content = {
                    BottomNavigationContents(
                        navDest = navDest,
                        bottomDrawerState = drawerState,
                        onNavItemClick = onNavItemClick
                    )
                }
            )
        }
    ) { paddingValues ->
        BottomDrawer(
            drawerContent = {
                AddAddressPanel(
                    closePanel = {
                        scope.launch {
                            drawerState.close()
                        }
                    }
                )
            },
            drawerState = drawerState,
            scrimColor = Color.Transparent,
            drawerBackgroundColor = Color.Transparent,
            gesturesEnabled = false
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colors.primary)
            ) {
                screeContent()
            }
        }
    }
}

@Composable
fun MainAppFab(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = {
            onClick()
        },
        shape = RoundedDiamondFabShape(8.dp),
        backgroundColor = HotPink
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            tint = MaterialTheme.colors.onPrimary,
            contentDescription = ""
        )
    }
}

@Composable
fun BottomNavigationContents(
    navDest: NavDestination?,
    bottomDrawerState: BottomDrawerState,
    onNavItemClick: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary
    ) {
        BottomNavigationItem(
            icon = {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(
                        id = R.drawable.ic_gallery_icon_src
                    ),
                    contentDescription = "Gallery"
                )
            },
            selectedContentColor = Turquoise,
            unselectedContentColor = Turquoise.copy(0.6f),
            alwaysShowLabel = false,
            selected = navDest?.hierarchy?.any { it.route == Gallery.route } == true,
            onClick = {
                onNavItemClick(Gallery.route)

                scope.launch {
                    bottomDrawerState.close()
                }
            }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(
                        id = R.drawable.ic_keys_icon_src
                    ),
                    contentDescription = "Addresses"
                )
            },
            selectedContentColor = Turquoise,
            unselectedContentColor = Turquoise.copy(0.6f),
            alwaysShowLabel = false,
            selected = navDest?.hierarchy?.any { it.route == Accounts.route } == true,
            onClick = { onNavItemClick(Accounts.route) }
        )
    }
}
