package com.creativedrewy.slideshowlivewallpaper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.creativedrewy.mozart.MozartWallpaperService
import com.creativedrewy.sharedui.NftImageViewer
import com.creativedrewy.sharedui.VideoWallpaperViewer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import javax.inject.Inject

@AndroidEntryPoint
class SlideshowWallpaperService : MozartWallpaperService() {

    @Inject
    lateinit var viewModel: SlideshowGalleryViewModel

    override val wallpaperContents: @Composable ((OffsetValues) -> Unit)
        get() = {
            val items by viewModel.wallpaperItems.collectAsState(initial = emptyList())

            var targetBgColor by remember { mutableStateOf(Color.LightGray) }
            val animatedBgColor by animateColorAsState(
                targetValue = targetBgColor,
                animationSpec = tween(800),
                label = "bgColorAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(animatedBgColor)
            ) {
                if (items.isNotEmpty()) {
                    var currentIndex by remember { mutableIntStateOf(0) }
                    var currentVideoDurationMs by remember { mutableLongStateOf(0L) }

                    val safeIndex = currentIndex % items.size
                    val currentItem = items[safeIndex]
                    val isCurrentVideo = viewModel.isVideoItem(currentItem)

                    LaunchedEffect(items.size) {
                        currentIndex = 0
                    }

                    LaunchedEffect(safeIndex) {
                        currentVideoDurationMs = 0L
                    }

                    LaunchedEffect(safeIndex, items.size, isCurrentVideo) {
                        if (items.isEmpty()) return@LaunchedEffect

                        if (!isCurrentVideo) {
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
                            .padding(horizontal = 8.dp),
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
                                if (viewModel.isVideoItem(currentItem)) {
                                    VideoWallpaperViewer(
                                        videoUrl = item.videoUrl,
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
                                .fillMaxSize(),
                            painter = painterResource(R.drawable.baroque_frame),
                            contentDescription = ""
                        )
                    }
                }
            }
        }
}

private const val SLIDESHOW_IMAGE_DISPLAY_MS = 8000L
private const val SLIDESHOW_VIDEO_MIN_PLAY_MS = 10_000L
