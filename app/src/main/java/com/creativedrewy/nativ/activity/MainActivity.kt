package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.House
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import com.creativedrewy.nativ.ui.AddAddressPanel
import com.creativedrewy.nativ.ui.AddressListScreen
import com.creativedrewy.nativ.ui.BackHandler
import com.creativedrewy.nativ.ui.GalleryList
import com.creativedrewy.nativ.ui.theme.NATIVTheme
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
                 modifier = Modifier.fillMaxWidth()
                     .height(90.dp)
                     .background(Color.Magenta)
             ) {

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
                gesturesEnabled = false
            ) {
                when (screenState.value) {
                    Gallery.route -> GalleryList()
                    Accounts.route -> AddressListScreen()
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
                cutoutShape = RoundedCornerShape(50),
                content = {
                    BottomNavigationContents(screenState)
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
    val animatedFloatState = animateFloatAsState(
        targetValue = if (screenState.value == Gallery.route) 0f else 1.0f
    )

    FloatingActionButton(
        modifier = Modifier.scale(animatedFloatState.value),
        onClick = {
            if (screenState.value == Accounts.route) { onClick() }
        },
        shape = RoundedCornerShape(50),
        backgroundColor = Color(0xFFFF8C00)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            ""
        )
    }
}

@Composable
fun BottomNavigationContents(
    screenState: MutableState<String>
) {
    BottomNavigation {
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.House,
                    contentDescription = "Gallery"
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.7f),
            alwaysShowLabel = false,
            selected = screenState.value == Gallery.route,
            onClick = { screenState.value = Gallery.route }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Addresses"
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.7f),
            alwaysShowLabel = false,
            selected = screenState.value == Accounts.route,
            onClick = { screenState.value = Accounts.route }
        )
    }
}