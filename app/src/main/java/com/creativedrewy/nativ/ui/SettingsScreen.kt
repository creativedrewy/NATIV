package com.creativedrewy.nativ.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.LightPurple
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.AddressListViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onConnectWallet: () -> Unit,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.loadAddresses()
    }

    var selectedIndex by remember { mutableStateOf(0) }
    var address by remember { mutableStateOf(TextFieldValue("")) }
    val addressInteractionState = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.stars_bg),
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 44.dp,
                    end = 16.dp,
                    start = 16.dp)
        ) {
            // Top bar with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Turquoise
                    )
                }
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Connect wallet button
            Button(
                onClick = onConnectWallet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = LightPurple
                ),
                elevation = ButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = "Connect Wallet",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Connect Wallet",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

//            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                text = "---- or ----",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
            )

            // Add address section
            Text(
                text = "Add Solana Public Key:",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = address,
                    interactionSource = addressInteractionState,
                    singleLine = true,
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "Add Address"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    onValueChange = { address = it },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = HotPink,
                        unfocusedBorderColor = LightPurple,
                        leadingIconColor = LightPurple,
                        cursorColor = Color.White
                    )
                )

                IconButton(
                    onClick = {
                        if (address.text.isNotEmpty()) {
                            val ticker = viewState.supportedChains[selectedIndex].ticker
                            viewModel.saveAddress(address.text, ticker)

                            address = TextFieldValue("")
                            selectedIndex = 0
                            keyboardController?.hide()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Address",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = HotPink,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Address list
            Text(
                text = "Your Addresses",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewState.userAddresses) { addr ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedCircleImage(
                            imageRes = addr.chainLogoRes,
                            size = 36.dp,
                            outlineWidth = 2.dp,
                            outlineColor = Turquoise,
                            backgroundColor = LightPurple
                        )
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            style = MaterialTheme.typography.h6,
                            text = viewModel.formatAddress(addr.address),
                            maxLines = 1,
                            color = MaterialTheme.colors.onPrimary
                        )
                        IconButton(
                            onClick = { viewModel.deleteAddress(addr.address, addr.chainTicker) }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete",
                                modifier = Modifier.size(24.dp),
                                tint = Turquoise
                            )
                        }
                    }
                }
            }
        }
    }
}
