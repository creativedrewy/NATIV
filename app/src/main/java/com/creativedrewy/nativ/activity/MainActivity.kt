package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
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
    val screenState = rememberSaveable { mutableStateOf(Accounts.route) }

    val scope = rememberCoroutineScope()
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
fun AddAddressPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Color.White)
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 48.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            var address by remember { mutableStateOf(TextFieldValue("")) }
            val addressInteractionState = remember { MutableInteractionSource() }
            var selectedTicker by remember { mutableStateOf("none") }

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = address,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = "Add Address"
                    )
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onValueChange = { address = it },
                interactionSource = addressInteractionState,
            )
            IconButton(
                onClick = {
                    //This isn't ideal, but hard to get "initial" selected chain
//                                    val ticker = if (selectedTicker == "none") {
//                                        viewState.supportedChains.firstOrNull()?.ticker ?: ""
//                                    } else {
//                                        selectedTicker
//                                    }
//
//                                    viewModel.saveAddress(address.text, ticker)
//                                    address = TextFieldValue("")
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Address",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color.Black,
                            shape = CircleShape
                        )
                )
            }
        }
    }
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