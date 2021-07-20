package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.House
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.creativedrewy.nativ.ui.AddAddressPanel
import com.creativedrewy.nativ.ui.AddressListScreen
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
    val screenState = rememberSaveable { mutableStateOf(Accounts.route) }
    val drawerState = rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                elevation = 0.dp,
                title = {
                    Text(text = "NATIV")
                }
            )
        },
        content = {
            BottomDrawer(
                drawerContent = {
                    AddAddressPanel()
                },
                drawerState = drawerState
            ) {
                when (screenState.value) {
                    Gallery.route -> GalleryList()
                    Accounts.route -> AddressListScreen()
                }
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
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
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