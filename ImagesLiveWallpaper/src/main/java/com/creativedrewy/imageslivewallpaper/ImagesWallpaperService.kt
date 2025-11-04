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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.creativedrewy.mozart.MozartWallpaperService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay


//val images = listOf(
//    "https://arweave.net/e7bJrc4k-dQtGVS1rxHlCWiHjdQIM82d4h8OdZ-ioVM?ext=png",
//    "https://arweave.net/cqI0lELJFasPoJjjg-dZoUG1LJDxAa3hq4s8IVvYVgo?ext=png",
//    "https://arweave.net/ghHzqkyF_gg6K3HuB8xTxi4ZRSZ7DPtpUmglWOLch9c?ext=png"
//)
//
//val imageLoader = ImageLoader.Builder(context)
//    .build()
//    .diskCache
//
//private fun loadImages() {
//    bitmaps.clear()
//    images.forEach { url ->
//        try {
//            val rawPath: Path? = imageLoader?.openEditor(url)?.data
//            rawPath?.let { path ->
//                val pathStrSource = path.name.replace(".tmp", "")
//                val cachedImage = context.cacheDir.toString() + "/" + pathStrSource
//
//                val bitmap = BitmapFactory.decodeFile(cachedImage)
//                bitmap?.let { bmp ->
//                    val scaledBitmap = Bitmap.createScaledBitmap(
//                        bmp,
//                        (squareSize.toInt()),
//                        (squareSize.toInt()),
//                        true
//                    )
//
//                    bitmaps.add(scaledBitmap)
//                    if (bmp != scaledBitmap) {
//                        bmp.recycle()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("ImagesWallpaper", "Error loading image: $url", e)
//        }
//    }
//}

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