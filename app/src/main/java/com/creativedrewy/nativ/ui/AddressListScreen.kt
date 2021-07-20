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
                                modifier = Modifier.size(24.dp)
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

//@Composable
//fun ChainSelectDropDown(
//    chainItems: List<SupportedChain>,
//    onSelect: (String) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//    var selectedIndex by remember { mutableStateOf(0) }
//
//    Box {
//        Image(
//            modifier = Modifier
//                .size(36.dp)
//                .padding(end = 8.dp)
//                .clickable { expanded = true },
//            painter = painterResource(
//                id = chainItems[selectedIndex].iconRes
//            ),
//            contentDescription = chainItems[selectedIndex].name
//        )
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//        ) {
//            chainItems.forEachIndexed { index, item ->
//                DropdownMenuItem(
//                    onClick = {
//                        onSelect(chainItems[index].ticker)
//
//                        selectedIndex = index
//                        expanded = false
//                    }
//                ) {
//                    Row {
//                        Image(
//                            modifier = Modifier
//                                .size(24.dp)
//                                .padding(end = 8.dp)
//                                .clickable { expanded = true },
//                            painter = painterResource(
//                                id = item.iconRes
//                            ),
//                            contentDescription = ""
//                        )
//                        Text(text = item.name)
//                    }
//                }
//            }
//        }
//    }
//}