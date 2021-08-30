package com.creativedrewy.nativ.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.viewmodel.Image
import com.creativedrewy.nativ.viewmodel.ImageAndVideo
import com.creativedrewy.nativ.viewmodel.Model3d
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.google.accompanist.glide.rememberGlidePainter
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
    nftProps: NftViewProps,
    outlineColor: Color,
    imageOnlyMode: Boolean = false,
    alpha: Float = 1.0f
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
    ) {
        when {
            imageOnlyMode || nftProps.assetType is Image -> {
                ImageViewer(nftProps)
            }
            nftProps.assetType is ImageAndVideo -> {
                VideoViewer(nftProps)
            }
            nftProps.assetType is Model3d -> {
                Model3dViewer(nftProps)
            }
        }
    }
}

@Composable
fun ImageViewer(
    nftProps: NftViewProps
) {
    Image(
        contentScale = ContentScale.Fit,
        painter = rememberGlidePainter(
            request = nftProps.displayImageUrl
        ),
        contentDescription = "Nft Image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
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
                        useController = false
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
    nftProp: NftViewProps
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