package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.*
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    companion object {
        init {
            Utils.init()
        }
    }

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NATIVTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    AppScreenContent()
                }
            }
        }
    }
}

sealed class AppScreen(
    val route: String
)

object Gallery : AppScreen("gallery")
object Accounts : AppScreen("accounts")

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalComposeUiApi
@Composable
fun AppScreenContent() {
    val scope = rememberCoroutineScope()
    val screenState = rememberSaveable { mutableStateOf(Gallery.route) }
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher?.let { dispatch ->
        BackHandler(
            backDispatcher = dispatch,
            enabled = drawerState.isExpanded
        ) {
            scope.launch {
                drawerState.close()
            }
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
        content = {
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
                    when (screenState.value) {
                        Gallery.route -> GalleryList()
                        Accounts.route -> AddressListScreen()
                    }
                }
            }
        },
        floatingActionButton = {
            MainAppFab(
                screenState = screenState
            ) {
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
                    BottomNavigationContents(screenState, drawerState)
                }
            )
        }
    )
}

@Composable
fun MainAppFab(
    screenState: MutableState<String>,
    onClick: () -> Unit
) {
    //TODO: For some reason trying to animate the FAB freezes some devices. Revisit later.
//    val animatedFloatState = animateFloatAsState(
//        targetValue = if (screenState.value == Gallery.route) 0f else 1.0f
//    )

    FloatingActionButton(
        //modifier = Modifier.scale(animatedFloatState.value),
        onClick = {
            if (screenState.value == Accounts.route) { onClick() }
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
    screenState: MutableState<String>,
    bottomDrawerState: BottomDrawerState
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
            selected = screenState.value == Gallery.route,
            onClick = {
                screenState.value = Gallery.route
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
            selected = screenState.value == Accounts.route,
            onClick = { screenState.value = Accounts.route }
        )
    }
}