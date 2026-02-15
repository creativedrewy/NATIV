package com.creativedrewy.sharedui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest

/**
 * Displays an NFT image with a blurred background fill and a fit-to-size foreground.
 */
@Composable
fun NftImageViewer(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    showBlurredBackground: Boolean = true
) {
    val context = LocalContext.current
    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
    }

    if (showBlurredBackground) {
        // Blurred background — crops to fill the area
        SubcomposeAsyncImage(
            model = imageRequest,
            contentDescription = null,
            modifier = modifier
                .fillMaxSize()
                .blur(radius = 16.dp),
            contentScale = ContentScale.Crop
        )
    }

    // Foreground image — fits within the area preserving aspect ratio
    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}
