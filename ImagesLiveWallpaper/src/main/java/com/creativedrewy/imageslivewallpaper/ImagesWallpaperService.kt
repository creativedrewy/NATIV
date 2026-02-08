package com.creativedrewy.imageslivewallpaper

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.creativedrewy.mozart.MozartWallpaperService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class ImagesWallpaperService: MozartWallpaperService() {

    @Inject
    lateinit var viewModel: WallpaperGalleryViewModel

    override val wallpaperContents: @Composable ((OffsetValues) -> Unit)
        get() = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray)
            ) {
                Log.v("Andrew", "::: You have a value ${viewModel.blah}")

                val colors = remember {
                    listOf(
                        Color(0xFFE57373),
                        Color(0xFF64B5F6),
                        Color(0xFF81C784),
                        Color(0xFFFFD54F),
                        Color(0xFFBA68C8)
                    )
                }

                var currentIndex by remember { mutableStateOf(0) }

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(5000)
                        currentIndex = (currentIndex + 1) % colors.size
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = currentIndex,
                        transitionSpec = {
                            slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(800)
                            ) togetherWith slideOutHorizontally(
                                targetOffsetX = { fullWidth -> -fullWidth },
                                animationSpec = tween(800)
                            )
                        },
                        label = "colorSquareTransition"
                    ) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors[index])
                        )
                    }
                }
            }
        }
}