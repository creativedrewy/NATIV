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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.DarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.ShimmerBlue
import com.creativedrewy.nativ.viewmodel.*
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer

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
            imageOnlyMode || nftProps.props.assetType is Image -> {
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

    SubcomposeAsyncImage(
        model = imageRequest,
        contentDescription = "Nft Image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
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

@Composable
fun VideoViewer(
    nftProps: NftViewProps
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        val exoPlayer = remember {
            SimpleExoPlayer.Builder(context).build()
        }

        DisposableEffect(
            AndroidView(
                modifier = Modifier
                    .align(Alignment.Center),
                factory = {
                    StyledPlayerView(it).apply {
                        hideController()
                        setShowBuffering(StyledPlayerView.SHOW_BUFFERING_ALWAYS)

                        player = exoPlayer
                    }
                }
            )
        ) {
            onDispose {
                exoPlayer.release()
            }
        }

        exoPlayer.playWhenReady = true
        exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
        exoPlayer.setMediaItem(MediaItem.fromUri(nftProps.videoUrl))
        exoPlayer.prepare()
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
                    viewer.scene.indirectLight = KtxLoader.createIndirectLight(viewer.engine, ibl)
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