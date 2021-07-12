
package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.House
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.creativedrewy.nativ.ui.GalleryList
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.NftGalleryViewModel
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: NftGalleryViewModel by viewModels()

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
                    ScreenLayout {
                        GalleryList()
                    }
                }
            }
        }

        viewModel.loadNfts()
    }
}

@Composable
fun AppScreenContent() {

    //val homeScreenState = rememberSaveable { mutableStateOf(BottomNavType.HOME) }

}

@Composable
fun ScreenLayout(
    content: @Composable () -> Unit
) {
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
            content()
        },
        bottomBar = {
            BottomNavigationContents()
        }
    )
}

@Composable
fun BottomNavigationContents() {
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
            selected = false,
            onClick = { }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Accounts"
                )
            },
            label = {
                Text(
                    text = "Accounts"
                )
            },
            selectedContentColor = Color.White,
            unselectedContentColor = Color.White.copy(0.7f),
            alwaysShowLabel = true,
            selected = false,
            onClick = { }
        )
    }
}