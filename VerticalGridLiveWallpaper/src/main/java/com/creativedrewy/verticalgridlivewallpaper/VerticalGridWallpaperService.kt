package com.creativedrewy.verticalgridlivewallpaper

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.mozart.MozartWallpaperService
import com.creativedrewy.sharedui.IMAGE_DISPLAY_MS
import com.creativedrewy.sharedui.VideoWallpaperViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import javax.inject.Inject
import kotlin.math.roundToInt

private const val GRID_COLS = 2
private const val GRID_ROWS = 4
private const val GRID_SIZE = GRID_COLS * GRID_ROWS // 8

private const val SCALE_ANIM_MS = 600
private const val PAUSE_BETWEEN_FEATURES_MS = 3_000L

@AndroidEntryPoint
class VerticalGridWallpaperService : MozartWallpaperService() {

    @Inject
    lateinit var viewModel: VerticalGridViewModel

    override val wallpaperContents: @Composable ((OffsetValues) -> Unit)
        get() = {
            val items by viewModel.wallpaperItems.collectAsState(initial = emptyList())

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                if (items.isNotEmpty()) {
                    val gridItems = items.take(GRID_SIZE)
                    VerticalGridContent(gridItems)
                }
            }
        }

    @Composable
    private fun VerticalGridContent(items: List<com.creativedrewy.solananft.viewmodel.NftViewProps>) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current

            val screenWidthPx = with(density) { maxWidth.toPx() }
            val screenHeightPx = with(density) { maxHeight.toPx() }

            // Square cells that fit within the screen in both dimensions
            val cellSizePx = minOf(screenWidthPx / GRID_COLS, screenHeightPx / GRID_ROWS)
            val cellWidthPx = cellSizePx
            val cellHeightPx = cellSizePx
            val gridWidthPx = cellWidthPx * GRID_COLS
            val gridHeightPx = cellHeightPx * GRID_ROWS
            val gridOffsetXPx = (screenWidthPx - gridWidthPx) / 2f
            val gridOffsetYPx = (screenHeightPx - gridHeightPx) / 2f

            // ----------- Featured-item state -----------
            // -1 means nothing is featured right now
            var featuredIndex by remember { mutableIntStateOf(-1) }
            var videoDurationMs by remember { mutableLongStateOf(0L) }

            // Animation progress: 0 = in-grid, 1 = full-screen
            val animProgress = remember { Animatable(0f) }

            // Orchestrator: pick random item → scale up → display → scale back → pause → repeat
            LaunchedEffect(items.size) {
                if (items.isEmpty()) return@LaunchedEffect

                // Initial short pause before the first feature
                delay(PAUSE_BETWEEN_FEATURES_MS)

                while (isActive) {
                    // Pick a random index
                    val nextIndex = (0 until items.size).random()
                    featuredIndex = nextIndex
                    videoDurationMs = 0L

                    // Animate scale up
                    animProgress.snapTo(0f)
                    animProgress.animateTo(1f, tween(SCALE_ANIM_MS))

                    // Show content for the appropriate duration
                    val isVideo = viewModel.isVideoItem(items[nextIndex])
                    if (isVideo) {
                        // Wait for video duration to be known, then wait for it to finish
                        while (isActive && videoDurationMs <= 0L) {
                            delay(100)
                        }
                        delay(videoDurationMs)
                    } else {
                        delay(IMAGE_DISPLAY_MS)
                    }

                    // Animate scale back down
                    animProgress.animateTo(0f, tween(SCALE_ANIM_MS))

                    featuredIndex = -1

                    // Pause before picking the next item
                    delay(PAUSE_BETWEEN_FEATURES_MS)
                }
            }

            // ----------- Grid layer -----------
            items.forEachIndexed { index, item ->
                val col = index % GRID_COLS
                val row = index / GRID_COLS

                val cellX = gridOffsetXPx + col * cellWidthPx
                val cellY = gridOffsetYPx + row * cellHeightPx

                // If this cell is the featured one, hide it from the grid (it's drawn on top)
                if (index == featuredIndex) return@forEachIndexed

                GridCell(
                    imageUrl = item.displayImageUrl,
                    contentDescription = item.name,
                    xPx = cellX,
                    yPx = cellY,
                    widthPx = cellWidthPx,
                    heightPx = cellHeightPx
                )
            }

            // ----------- Dim overlay -----------
            if (featuredIndex >= 0) {
                val alpha = animProgress.value * 0.6f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = alpha))
                )
            }

            // ----------- Featured item (animated) -----------
            // Scales from grid cell to a screen-width square centered on screen,
            // preserving the image's aspect ratio (ContentScale.Fit) so the grid
            // peeks through at the top and bottom.
            if (featuredIndex >= 0 && featuredIndex < items.size) {
                val item = items[featuredIndex]
                val col = featuredIndex % GRID_COLS
                val row = featuredIndex / GRID_COLS

                val cellX = gridOffsetXPx + col * cellWidthPx
                val cellY = gridOffsetYPx + row * cellHeightPx

                // Target: screen-width square, vertically centered
                val targetSize = screenWidthPx
                val targetX = 0f
                val targetY = (screenHeightPx - targetSize) / 2f

                val progress = animProgress.value

                val currentX = lerp(cellX, targetX, progress)
                val currentY = lerp(cellY, targetY, progress)
                val currentW = lerp(cellWidthPx, targetSize, progress)
                val currentH = lerp(cellHeightPx, targetSize, progress)

                val isVideo = viewModel.isVideoItem(item)

                Box(
                    modifier = Modifier
                        .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                        .width(with(density) { currentW.toDp() })
                        .height(with(density) { currentH.toDp() })
                        .clip(RectangleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (progress > 0.9f && isVideo) {
                        // Only show video player once nearly full-screen
                        VideoWallpaperViewer(
                            videoUrl = item.videoUrl,
                            onDurationKnown = { duration ->
                                videoDurationMs = duration
                            }
                        )
                    } else {
                        // Static image preserving aspect ratio
                        val context = LocalContext.current
                        val imageRequest = remember(item.displayImageUrl) {
                            ImageRequest.Builder(context)
                                .data(item.displayImageUrl)
                                .build()
                        }
                        SubcomposeAsyncImage(
                            model = imageRequest,
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun GridCell(
        imageUrl: String,
        contentDescription: String,
        xPx: Float,
        yPx: Float,
        widthPx: Float,
        heightPx: Float
    ) {
        val density = LocalDensity.current
        val context = LocalContext.current

        val imageRequest = remember(imageUrl) {
            ImageRequest.Builder(context)
                .data(imageUrl)
                .build()
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(xPx.roundToInt(), yPx.roundToInt()) }
                .width(with(density) { widthPx.toDp() })
                .height(with(density) { heightPx.toDp() })
        ) {
            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}
