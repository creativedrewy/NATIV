package com.creativedrewy.nativ.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun LineSwipeRefreshIndicator(
    swipeRefreshState: SwipeRefreshState,
    triggerDistance: Dp,
    lineColor: Color
) {
    Box(
        modifier = Modifier
            .height(2.dp)
    ) {
        when {
            !swipeRefreshState.isRefreshing -> {
                val triggerDist = with(LocalDensity.current) { triggerDistance.toPx() }
                val progress = (swipeRefreshState.indicatorOffset / triggerDist).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = lineColor,
                    backgroundColor = Color.Transparent
                )
            }
        }
    }
}