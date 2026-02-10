package com.creativedrewy.imageslivewallpaper

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.mozart.MozartWallpaperService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class ImagesWallpaperService : MozartWallpaperService() {

    @Inject
    lateinit var viewModel: WallpaperGalleryViewModel

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

                    val safeIndex = currentIndex % items.size

                    LaunchedEffect(items.size) {
                        currentIndex = 0
                    }

                    // Apply cached color immediately when index changes
//                    LaunchedEffect(safeIndex) {
//                        val url = items[safeIndex].imageUrl
//                        colorCache[url]?.let { targetBgColor = it }
//                    }

                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(8000)
                            if (items.isNotEmpty()) {
                                currentIndex = (currentIndex + 1) % items.size
                            }
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
                            val context = LocalContext.current

                            val imageRequest = remember(item.imageUrl) {
                                ImageRequest.Builder(context)
                                    .data(item.imageUrl)
                                    .build()
                            }

                            Box(
                                modifier = Modifier
                                    .width(245.dp)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                // Blurred background — crops to fill the square
                                SubcomposeAsyncImage(
                                    model = imageRequest,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .blur(radius = 16.dp),
                                    contentScale = ContentScale.Crop
                                )

                                // Foreground image — fits within the square preserving aspect ratio
                                SubcomposeAsyncImage(
                                    model = imageRequest,
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    onSuccess = { state ->
                                        try {
//                                            val bitmap = state.result.image.toBitmap()
//                                            Log.v("Andrew", "::: Your bitmap is $bitmap")
//
//                                            var redBucket: Long = 0
//                                            var greenBucket: Long = 0
//                                            var blueBucket: Long = 0
//                                            var pixelCount: Long = 0
//
//                                            for (y in 0 ..< bitmap.getHeight()) {
//                                                for (x in 0 ..< bitmap.getWidth()) {
//                                                    val c = bitmap.getPixel(x, y)
//
//                                                    pixelCount++
//                                                    val red = (c shr 16) and 0xFF
//                                                    val green = (c shr 8) and 0xFF
//                                                    val blue = c and 0xFF
//                                                    redBucket += red
//                                                    greenBucket += green
//                                                    blueBucket += blue
//                                                }
//                                            }
//
//                                            val red = (redBucket / pixelCount).toInt()
//                                            val green = (greenBucket / pixelCount).toInt()
//                                            val blue = (blueBucket / pixelCount).toInt()
//
//                                            targetBgColor = Color(red, green, blue)
                                        } catch (_: Exception) {
                                            // Ignore color extraction failures
                                        }
                                    }
                                )
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
