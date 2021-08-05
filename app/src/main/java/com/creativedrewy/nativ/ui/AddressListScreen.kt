package com.creativedrewy.nativ.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.ui.theme.LightPurple
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.AddressListViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressListScreen(
    viewModel: AddressListViewModel = viewModel()
) {
    val viewState = viewModel.viewState.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                        OutlinedCircleImage(
                            imageRes = addr.chainLogoRes,
                            size = 36.dp,
                            outlineWidth = 2.dp,
                            outlineColor = Turquoise,
                            backgroundColor = LightPurple
                        )
                        Text(
                            modifier = Modifier.weight(1f)
                                .padding(start = 8.dp),
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
