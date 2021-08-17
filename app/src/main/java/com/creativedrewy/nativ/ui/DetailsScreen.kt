package com.creativedrewy.nativ.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.DetailsViewModel
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun DetailsScreen(
    nftId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNftDetails(nftId)
        }
    )

    val loadedNft by viewModel.viewState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(CardDarkBlue)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color.Gray)
        )
        Text(
            modifier = Modifier
                .padding(
                    top = 8.dp
                ),
            text = loadedNft.name,
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            modifier = Modifier
                .padding(
                    top = 8.dp
                ),
            text = loadedNft.description,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary
        )
        Text(
            modifier = Modifier
                .padding(
                    top = 8.dp
                ),
            text = loadedNft.siteUrl,
            style = MaterialTheme.typography.body2,
            color = Turquoise
        )
        Text(
            modifier = Modifier
                .padding(
                    top = 8.dp
                ),
            text = "Attributes",
            style = MaterialTheme.typography.h4,
            color = MaterialTheme.colors.onPrimary
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            loadedNft.attributes.forEach { attrib ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            border = BorderStroke(2.dp, HotPink),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                    ) {
                        Text(
                            text = attrib.name
                        )
                        Text(
                            text = attrib.value
                        )
                    }
                }
            }
        }
    }
}
