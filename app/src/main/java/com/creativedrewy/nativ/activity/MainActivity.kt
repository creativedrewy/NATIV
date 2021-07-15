
package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.House
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.creativedrewy.nativ.ui.AddressesScreen
import com.creativedrewy.nativ.ui.GalleryList
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    companion object {
        init { Utils.init() }
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

object Gallery: AppScreen("gallery")
object Accounts: AppScreen("accounts")

@ExperimentalComposeUiApi
@Composable
fun AppScreenContent() {
    val screenState = rememberSaveable { mutableStateOf(Accounts.route) }

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
            when (screenState.value) {
                Gallery.route -> GalleryList()
                Accounts.route -> AddressesScreen()
            }
        },
        bottomBar = {
            BottomNavigationContents(screenState)
        }
    )
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
            label = {
                Text(
                    text = "Gallery"
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.7f),
            alwaysShowLabel = true,
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
            label = {
                Text(
                    text = "Addresses"
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.7f),
            alwaysShowLabel = true,
            selected = screenState.value == Accounts.route,
            onClick = { screenState.value = Accounts.route }
        )
    }
}