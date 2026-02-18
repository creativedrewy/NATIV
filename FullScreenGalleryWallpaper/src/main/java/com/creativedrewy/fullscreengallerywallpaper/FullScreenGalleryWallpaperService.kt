package com.creativedrewy.fullscreengallerywallpaper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.mozart.MozartWallpaperService
import com.creativedrewy.solananft.viewmodel.NftViewProps
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject

private const val FULLSCREEN_IMAGE_DISPLAY_MS = 15_000L
private const val FULLSCREEN_CROSSFADE_MS = 800

@AndroidEntryPoint
class FullScreenGalleryWallpaperService : MozartWallpaperService() {

    @Inject
    lateinit var viewModel: FullScreenGalleryViewModel

    override val wallpaperContents: @Composable ((OffsetValues) -> Unit)
        get() = {
            val items by viewModel.wallpaperItems.collectAsState(initial = emptyList())

            FullScreenGalleryWallpaperContents(
                items = items
            )
        }
}

@Composable
fun FullScreenGalleryWallpaperContents(
    items: List<NftViewProps>
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (items.isNotEmpty()) {
            var currentIndex by remember { mutableIntStateOf(0) }
            val safeIndex = currentIndex % items.size

            LaunchedEffect(items.size) {
                currentIndex = 0
            }

            LaunchedEffect(safeIndex, items.size) {
                if (items.isEmpty()) return@LaunchedEffect
                delay(FULLSCREEN_IMAGE_DISPLAY_MS)

                if (items.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % items.size
                }
            }

            AnimatedContent(
                targetState = safeIndex,
                transitionSpec = {
                    fadeIn(tween(FULLSCREEN_CROSSFADE_MS)) togetherWith
                        fadeOut(tween(FULLSCREEN_CROSSFADE_MS))
                },
                label = "fullscreenGalleryTransition"
            ) { index ->
                val item = items[index]

                FullScreenGalleryImage(
                    imageUrl = item.displayImageUrl,
                    contentDescription = item.name,
                    animationKey = index
                )
            }
        }
    }
}

@Composable
private fun FullScreenGalleryImage(
    imageUrl: String,
    contentDescription: String?,
    animationKey: Int
) {
    val context = LocalContext.current
    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
    }

    val density = LocalDensity.current
    val kenBurnsProgress = remember(animationKey) { Animatable(0f) }
    val maxOffsetPx = with(density) { 15.dp.toPx() }
    val startScale = 1.035f
    val endScale = 1.095f

    val (startX, startY, endX, endY) = remember(animationKey) {
        when (animationKey % 4) {
            0 -> arrayOf(-maxOffsetPx, -maxOffsetPx, maxOffsetPx, maxOffsetPx)
            1 -> arrayOf(maxOffsetPx, -maxOffsetPx, -maxOffsetPx, maxOffsetPx)
            2 -> arrayOf(-maxOffsetPx, maxOffsetPx, maxOffsetPx, -maxOffsetPx)
            else -> arrayOf(maxOffsetPx, maxOffsetPx, -maxOffsetPx, -maxOffsetPx)
        }
    }
    val progress = kenBurnsProgress.value
    val scale = lerp(startScale, endScale, progress)
    val offsetX = lerp(startX, endX, progress)
    val offsetY = lerp(startY, endY, progress)

    LaunchedEffect(animationKey) {
        kenBurnsProgress.snapTo(0f)
        kenBurnsProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(FULLSCREEN_IMAGE_DISPLAY_MS.toInt())
        )
    }
 
    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                val extraX = (size.width * (scaleX - 1f)) / 2f
                val extraY = (size.height * (scaleY - 1f)) / 2f
                translationX = offsetX.coerceIn(-extraX, extraX)
                translationY = offsetY.coerceIn(-extraY, extraY)
            },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PreviewFullScreenGalleryWallpaperContents() {
    val sampleItems = listOf(
        NftViewProps(
            id = UUID.randomUUID(),
            name = "Sample NFT",
            displayImageUrl = "https://placekitten.com/900/900",
            videoUrl = "null"
        ),
        NftViewProps(
            id = UUID.randomUUID(),
            name = "Video NFT",
            displayImageUrl = "https://placekitten.com/901/901",
            videoUrl = "https://www.example.com/samplevideo.mp4",
        )
    )

    FullScreenGalleryWallpaperContents(
        items = sampleItems
    )
}

@Preview(showBackground = true)
@Composable
fun FullScreenGalleryWallpaperContentsPreview() {
    PreviewFullScreenGalleryWallpaperContents()
}
private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}