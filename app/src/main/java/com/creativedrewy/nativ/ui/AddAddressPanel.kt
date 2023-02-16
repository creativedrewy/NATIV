package com.creativedrewy.nativ.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.LightPurple
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.AddressListViewModel

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddAddressPanel(
    closePanel: () -> Unit,
    viewModel: AddressListViewModel = hiltViewModel()
) {
    val viewState = viewModel.viewState.collectAsState().value

    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            )
            .padding(
                top = 24.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 48.dp
            )
    ) {
        Text(
            modifier = Modifier.padding(
                bottom = 8.dp
            ),
            text = "Public Key Address:",
            style = MaterialTheme.typography.h6
        )
        Row(
            modifier = Modifier.padding(
                bottom = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var address by remember { mutableStateOf(TextFieldValue("")) }
            val addressInteractionState = remember { MutableInteractionSource() }
            val keyboardController = LocalSoftwareKeyboardController.current

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
                        closePanel()
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = 4)
        ) {
            itemsIndexed(viewState.supportedChains) { index, chainItem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = index },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedCircleImage(
                        imageRes = chainItem.iconRes,
                        size = 64.dp,
                        outlineWidth = 4.dp,
                        outlineColor = if (index == selectedIndex) Turquoise else LightPurple,
                        backgroundColor = LightPurple
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 4.dp
                            ),
                        text = chainItem.name,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
