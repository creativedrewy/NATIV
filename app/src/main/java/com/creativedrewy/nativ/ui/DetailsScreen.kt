package com.creativedrewy.nativ.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.viewmodel.DetailsViewModel

@Composable
fun DetailsScreen(
    nftId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(CardDarkBlue)
    )
}
