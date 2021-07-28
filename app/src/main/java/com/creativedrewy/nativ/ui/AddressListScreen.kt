package com.creativedrewy.nativ.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.AddressListViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressListScreen(
    viewModel: AddressListViewModel = viewModel()
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
                                id = addr.chainLogoRes
                            ),
                            contentDescription = ""
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.h6,
                            text = formatAddress(addr.address)
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

fun formatAddress(srcAddr: String): String {
    return if (srcAddr.length >= 20) {
        srcAddr.take(10) + "..." + srcAddr.takeLast(10)
    } else {
        srcAddr
    }
}