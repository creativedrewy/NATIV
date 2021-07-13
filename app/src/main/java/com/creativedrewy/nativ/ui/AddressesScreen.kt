package com.creativedrewy.nativ.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.viewmodel.AddressesViewModel
import com.creativedrewy.nativ.viewmodel.SupportedChain

@Composable
fun AddressesScreen(
    viewModel: AddressesViewModel = viewModel()
) {
    val viewState = viewModel.viewState.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 64.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "Your Addresses",
                style = MaterialTheme.typography.h5,
            )
            LazyColumn() {
                items(viewState.userAddresses) { addr ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier.size(36.dp)
                                .padding(end = 8.dp),
                            painter = painterResource(
                                id = addr.chainDrawable
                            ),
                            contentDescription = ""
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.h6,
                            text = addr.addrPubKey
                        )
                        IconButton(
                            onClick = { }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomStart)
                .padding(
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            var address by remember { mutableStateOf(TextFieldValue("")) }
            val addressInteractionState = remember { MutableInteractionSource() }
            var selectedSymbol by remember { mutableStateOf("") }

            if (viewState.supportedChains.isNotEmpty()) {
                ChainSelectDropDown(
                    chainItems = viewState.supportedChains,
                    onSelect = { selected ->
                        selectedSymbol = selected
                    }
                )
            }

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
                    Log.v("SOL", "You are inserting $selectedSymbol, ${ address.text }")
                    viewModel.saveAddress(selectedSymbol, address.text)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Address",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
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
fun ChainSelectDropDown(
    chainItems: List<SupportedChain>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    //Be sure to "select" the first item in the list by default
    onSelect(chainItems[0].symbol)

    Box {
        Image(
            modifier = Modifier.size(36.dp)
                .padding(end = 8.dp)
                .clickable { expanded = true },
            painter = painterResource(
                id = chainItems[selectedIndex].iconRes
            ),
            contentDescription = chainItems[selectedIndex].name
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            chainItems.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        onSelect(chainItems[index].symbol)

                        selectedIndex = index
                        expanded = false
                    }
                ) {
                    Text(text = item.symbol)
                }
            }
        }
    }
}