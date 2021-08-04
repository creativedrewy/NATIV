package com.creativedrewy.nativ.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

@Composable
fun OutlinedCircleImage(
    imageRes: Int,
    size: Dp,
    outlineWidth: Dp,
    outlineColor: Color = Color.Transparent,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(size)
            .background(
                color = outlineColor
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(size - outlineWidth)
                .clip(CircleShape)
                .background(backgroundColor),
            painter = painterResource(
                id = imageRes
            ),
            contentDescription = ""
        )
    }
}