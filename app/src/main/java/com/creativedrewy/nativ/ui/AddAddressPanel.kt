package com.creativedrewy.nativ.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.viewmodel.AddressListViewModel

@ExperimentalComposeUiApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddAddressPanel(
    closePanel: () -> Unit,
    viewModel: AddressListViewModel = viewModel()
) {
    val viewState = viewModel.viewState.collectAsState().value

    var selectedIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
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
            val keyboardController = LocalSoftwareKeyboardController.current

            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = address,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Link,
                        contentDescription = "Add Address"
                    )
                },
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                onValueChange = { address = it },
                interactionSource = addressInteractionState,
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
                            color = Color.Black,
                            shape = CircleShape
                        )
                )
            }
        }

        LazyVerticalGrid(
            cells = GridCells.Fixed(count = 4)
        ) {
            itemsIndexed(viewState.supportedChains) { index, chainItem ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = index },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(64.dp)
                            .background(
                                color = if (index == selectedIndex) Color.Red else Color.White
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            painter = painterResource(
                                id = chainItem.iconRes
                            ),
                            contentDescription = ""
                        )
                    }
                    Text(text = chainItem.name)
                }
            }
        }
    }
}