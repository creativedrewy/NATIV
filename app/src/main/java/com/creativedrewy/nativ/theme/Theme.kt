package com.creativedrewy.nativ.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = DarkestBlue,
    primaryVariant = DarkBlue,
    secondary = HotPink,
    surface = DarkBlue,
    onPrimary = Color.White,
    onSurface = Color.White
)

@Composable
fun NATIVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    //For now NATIV is only dark theme
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}