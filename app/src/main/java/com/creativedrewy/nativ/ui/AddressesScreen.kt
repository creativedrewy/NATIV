package com.creativedrewy.nativ.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun AddressesScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 64.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp)
        ) {
            Text(
                text = "Your Addresses",
                style = MaterialTheme.typography.h5,
            )
            LazyColumn() {
                items(5) {
                    Text("Hello World")
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            var address by remember { mutableStateOf(TextFieldValue("")) }
            val addressInteractionState = remember { MutableInteractionSource() }

            OutlinedTextField(
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
                label = {
                    Text(text = "Add Blockchain Address")
                },
                onValueChange = { address = it },
                interactionSource = addressInteractionState,
            )
            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Address"
                )
            }
        }
    }
}