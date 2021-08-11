package com.creativedrewy.nativ.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

sealed class AppScreen(
    val route: String
)

object Gallery : AppScreen("gallery")
object Accounts : AppScreen("accounts")
object Details : AppScreen("details")

@ExperimentalAnimationApi
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun AppScreenContent() {
    val animNavController = rememberAnimatedNavController()

    fun navigate(route: String) {
        animNavController.navigate(route) {
            popUpTo(animNavController.graph.findStartDestination().id) {
                saveState = true
            }

            launchSingleTop = true
            restoreState = true
        }
    }

    AnimatedNavHost(
        navController = animNavController,
        startDestination = Gallery.route
    ) {
        composable(Gallery.route) {
            FabScreens(
                onNavItemClick = { navigate(it) }
            ) {
                GalleryList(
                    onDetailsNavigate = { }
                )
            }
        }
        composable(Accounts.route) {
            FabScreens(
                onNavItemClick = { navigate(it) }
            ) {
                AddressListScreen()
            }
        }
        composable(Details.route) { }
    }
}

@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
fun FabScreens(
    onNavItemClick: (String) -> Unit,
    screeContent: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

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
            MainAppFab {
                scope.launch {
                    drawerState.expand()
                }
            }
        },
        isFloatingActionButtonDocked = true,
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                cutoutShape = RoundedDiamondFabShape(8.dp),
                content = {
                    BottomNavigationContents(drawerState, onNavItemClick)
                }
            )
        }
    ) {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomNavigationContents(
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
//            selected = screenState.value == Gallery.route,
            selected = false,
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
            selected = false,
//            selected = screenState.value == Accounts.route,
            onClick = { onNavItemClick(Accounts.route) }
        )
    }
}
