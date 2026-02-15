package com.creativedrewy.slideshowlivewallpaper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.creativedrewy.mozart.MozartWallpaperService
import com.creativedrewy.sharedui.NftImageViewer
import com.creativedrewy.sharedui.VideoWallpaperViewer
import com.creativedrewy.solananft.viewmodel.NftViewProps
import com.creativedrewy.solananft.viewmodel.isVideoItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.UUID
import javax.inject.Inject

const val VIDEO_MIN_PLAY_MS = 10000

@AndroidEntryPoint
class SlideshowWallpaperService : MozartWallpaperService() {

    @Inject
    lateinit var viewModel: SlideshowGalleryViewModel

    override val wallpaperContents: @Composable ((OffsetValues) -> Unit)
        get() = {
            val items by viewModel.wallpaperItems.collectAsState(initial = emptyList())

            SlideshowWallpaperContents(
                bgColor = Color.Green,
                items = items
            )
        }
}

@Composable
fun SlideshowWallpaperContents(
    bgColor: Color,
    items: List<NftViewProps>
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        if (items.isNotEmpty()) {
            var currentIndex by remember { mutableIntStateOf(0) }
            var currentVideoDurationMs by remember { mutableLongStateOf(0L) }

            val safeIndex = currentIndex % items.size
            val currentItem = items[safeIndex]

            LaunchedEffect(items.size) {
                currentIndex = 0
            }

            LaunchedEffect(safeIndex) {
                currentVideoDurationMs = 0L
            }

            LaunchedEffect(safeIndex, items.size, currentItem.isVideoItem) {
                if (items.isEmpty()) return@LaunchedEffect

                if (!currentItem.isVideoItem) {
                    delay(SLIDESHOW_IMAGE_DISPLAY_MS)
                    if (items.isNotEmpty()) {
                        currentIndex = (currentIndex + 1) % items.size
                    }
                    return@LaunchedEffect
                }

                // Wait for video duration to be known
                while (isActive && currentVideoDurationMs <= 0L) {
                    delay(100)
                }

                val playDuration = if (currentVideoDurationMs >= SLIDESHOW_VIDEO_MIN_PLAY_MS) {
                    currentVideoDurationMs
                } else {
                    SLIDESHOW_VIDEO_MIN_PLAY_MS
                }

                delay(playDuration)
                if (items.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % items.size
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = safeIndex,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = tween(800)
                        ) togetherWith (
                                slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> -fullWidth },
                                    animationSpec = tween(800)
                                )
                                )
                    },
                    label = "nftWallpaperTransition"
                ) { index ->
                    val item = items[index]

                    Box(
                        modifier = Modifier
                            .width(245.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentItem.isVideoItem) {
                            VideoWallpaperViewer(
                                videoUrl = item.videoUrl,
                                repeatModeThreshold = VIDEO_MIN_PLAY_MS,
                                onDurationKnown = { duration ->
                                    currentVideoDurationMs = duration
                                }
                            )
                        } else {
                            NftImageViewer(
                                imageUrl = item.displayImageUrl,
                                contentDescription = item.name
                            )
                        }
                    }
                }

                Image(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = 15f
                            translationY = 15f
                        },
                    painter = painterResource(R.drawable.baroque_shadow),
                    contentDescription = ""
                )

                Image(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxSize(),
                    painter = painterResource(R.drawable.baroque_frame),
                    contentDescription = "",
//                    colorFilter = ColorFilter.tint(Color.Blue, blendMode = BlendMode.Color)
                )
            }
        }
    }
}


private const val SLIDESHOW_IMAGE_DISPLAY_MS = 8000L
private const val SLIDESHOW_VIDEO_MIN_PLAY_MS = 10_000L

@Composable
fun PreviewSlideshowWallpaperContents() {
    val sampleItems = listOf(
        NftViewProps(
            id = UUID.randomUUID(),
            name = "Sample NFT",
            displayImageUrl = "https://placekitten.com/400/400",
            videoUrl = "null"
        ),
        NftViewProps(
            id = UUID.randomUUID(),
            name = "Video NFT",
            displayImageUrl = "https://placekitten.com/401/401",
            videoUrl = "https://www.example.com/samplevideo.mp4",
        )
    )

    SlideshowWallpaperContents(
        bgColor = Color.LightGray,
        items = sampleItems
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SlideshowWallpaperContentsPreview() {
    PreviewSlideshowWallpaperContents()
}