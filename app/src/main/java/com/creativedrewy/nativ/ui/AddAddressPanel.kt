package com.creativedrewy.nativ.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

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