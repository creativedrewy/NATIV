package com.creativedrewy.nativ.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.chainsupport.nft.AnimatedImage
import com.creativedrewy.nativ.chainsupport.nft.Image
import com.creativedrewy.nativ.chainsupport.nft.ImageAndVideo
import com.creativedrewy.nativ.chainsupport.nft.Model3d
import com.creativedrewy.nativ.ui.theme.DarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.ShimmerBlue
import com.creativedrewy.nativ.viewmodel.PropsWithMedia
import com.creativedrewy.solananft.viewmodel.NftViewProps
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KTX1Loader
import com.google.android.filament.utils.ModelViewer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.nio.ByteBuffer
import kotlin.math.max

@ExperimentalComposeUiApi
@Composable
fun AssetViewer(
    nftProps: PropsWithMedia,
    outlineColor: Color,
    imageOnlyMode: Boolean = false,
    alpha: Float = 1.0f,
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .alpha(alpha)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(2.dp, outlineColor),
                shape = RoundedCornerShape(16.dp)
            )
            .placeholder(
                visible = isLoading,
                color = DarkBlue,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = ShimmerBlue,
                ),
            ),
    ) {
        when {
            imageOnlyMode || nftProps.props.assetType is Image || nftProps.props.assetType is AnimatedImage -> {
                ImageViewer(nftProps.props)
            }
            nftProps.props.assetType is ImageAndVideo -> {
                VideoViewer(nftProps.props)
            }
            nftProps.props.assetType is Model3d -> {
                Model3dViewer(nftProps)
            }
        }
    }
}

@Composable
fun ImageViewer(
    nftProps: NftViewProps
) {
    val context = LocalContext.current

    val imageRequest = remember(nftProps.displayImageUrl) {
        ImageRequest.Builder(context)
            .data(nftProps.displayImageUrl)
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // Blurred background fill — crops to fill the square
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
            contentDescription = "Nft Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = HotPink
                    )
                }
            }
        )
    }
}

@Composable
fun VideoViewer(
    nftProps: NftViewProps
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build()
        }
        var isPlaying by remember { mutableStateOf(false) }
        var positionMs by remember { mutableLongStateOf(0L) }
        var durationMs by remember { mutableLongStateOf(0L) }
        val sliderDuration = max(durationMs, 1L)

        LaunchedEffect(exoPlayer) {
            while (isActive) {
                positionMs = exoPlayer.currentPosition
                durationMs = exoPlayer.duration.coerceAtLeast(0L)
                isPlaying = exoPlayer.isPlaying
                delay(250)
            }
        }

        AndroidView(
            modifier = Modifier
                .align(Alignment.Center),
            factory = {
                PlayerView(it).apply {
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                    player = exoPlayer
                }
            }
        )

        DisposableEffect(exoPlayer) {
            onDispose {
                exoPlayer.release()
            }
        }

        LaunchedEffect(nftProps.videoUrl) {
            exoPlayer.playWhenReady = true
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            exoPlayer.setMediaItem(MediaItem.fromUri(nftProps.videoUrl))
            exoPlayer.prepare()
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    tint = Color.White,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }

            Slider(
                value = positionMs.toFloat().coerceIn(0f, sliderDuration.toFloat()),
                onValueChange = { newValue ->
                    positionMs = newValue.toLong()
                },
                onValueChangeFinished = {
                    exoPlayer.seekTo(positionMs)
                },
                valueRange = 0f..sliderDuration.toFloat(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(start = 48.dp, end = 8.dp)
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun Model3dViewer(
    nftProp: PropsWithMedia
) {
    fun readCompressedAsset(context: Context, assetName: String): ByteBuffer {
        val input = context.assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    var modelViewer by remember { mutableStateOf<ModelViewer?>(null) }

    LaunchedEffect(modelViewer) {
        while (true) {
            withFrameNanos { frameTimeNanos ->
                modelViewer?.render(frameTimeNanos)
            }
        }
    }

    AndroidView(
        { context ->
            LayoutInflater.from(context).inflate(
                R.layout.filament_host, FrameLayout(context), false
            ).apply {
                modelViewer = ModelViewer(this as SurfaceView)
                modelViewer?.let { viewer ->
                    val ibl = readCompressedAsset(context, "courtyard_8k_ibl.ktx")
                    viewer.scene.indirectLight = KTX1Loader.createIndirectLight(viewer.engine, ibl).indirectLight
                    viewer.scene.indirectLight?.intensity = 30_000.0f

                    viewer.scene.skybox = Skybox.Builder()
                        .color(0.035f, 0.035f, 0.035f, 1.0f)
                        .build(viewer.engine)

                    viewer.loadModelGlb(ByteBuffer.wrap(nftProp.mediaBytes))
                    viewer.transformToUnitCube()
                }
            }
        },
        modifier = Modifier.pointerInteropFilter {
            modelViewer?.onTouchEvent(it)
            true
        }
    )
}