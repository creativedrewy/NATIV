package com.creativedrewy.nativ.ui

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

class RoundedDiamondFabShape(
    private val cornerRadius: Dp
): Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                with(density) {
                    val radius = cornerRadius.toPx()
                    val cosRadius = 0.707f * radius

                    reset()
                    arcTo(
                        rect = Rect(
                            left = 0f,
                            top = (size.height / 2) - radius,
                            right = radius * 2,
                            bottom = (size.height / 2) + radius
                        ),
                        startAngleDegrees = 135f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(
                        x = size.width / 2 - cosRadius,
                        y = radius - cosRadius
                    )
                    arcTo(
                        rect = Rect(
                            left = (size.width / 2) - radius,
                            top = 0f,
                            right = (size.width / 2) + radius,
                            bottom = radius * 2
                        ),
                        startAngleDegrees = 225f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(
                        x = size.width - radius + cosRadius,
                        y = size.height / 2 - cosRadius
                    )
                    arcTo(
                        rect = Rect(
                            left = size.width - (radius * 2),
                            top = (size.height / 2) - radius,
                            right = size.width,
                            bottom = (size.height / 2) + radius
                        ),
                        startAngleDegrees = 315f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(
                        x = (size.width / 2) + cosRadius,
                        y = size.height - radius + cosRadius
                    )
                    arcTo(
                        rect = Rect(
                            left = (size.width / 2) - radius,
                            top = size.height - (radius * 2),
                            right = (size.width / 2) + radius,
                            bottom = size.height
                        ),
                        startAngleDegrees = 45f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    close()
                }
            }
        )
    }
}