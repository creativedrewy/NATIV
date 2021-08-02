package com.creativedrewy.nativ.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.creativedrewy.nativ.R

private val DarkColorPalette = darkColors(
    primary = DarkestBlue,
    primaryVariant = DarkBlue,
    secondary = HotPink,
    surface = DarkBlue,
    onPrimary = Color.White,
    onSurface = Color.White
)

val Lexend = FontFamily(
    Font(R.font.lexend_regular),
    Font(R.font.lexend_medium, FontWeight.Medium),
)

val NATIVTypography = Typography(
    defaultFontFamily = Lexend,
    h5 = TextStyle(
        fontFamily = Lexend,
        fontWeight = FontWeight.Medium,
        fontSize = 30.sp
    )
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
        typography = NATIVTypography,
        shapes = Shapes,
        content = content
    )
}