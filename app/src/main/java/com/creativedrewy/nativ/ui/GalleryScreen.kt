package com.creativedrewy.nativ.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.ShimmerColor
import com.creativedrewy.nativ.ui.theme.White
import com.creativedrewy.nativ.viewmodel.*
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer

@ExperimentalComposeUiApi
@Composable
fun GalleryList(
    viewModel: NftGalleryViewModel = viewModel()
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNfts()
        })

    val state by viewModel.viewState.collectAsState()

    val isLoading = state is Loading
    LazyColumn(
        modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 48.dp)
    ) {
        items(state.listItems) { nft ->
            GalleryItemCard(
                loading = isLoading,
                nftProps = nft
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun GalleryItemCard(
    loading: Boolean,
    nftProps: NftViewProps
) {
    Surface(
        modifier = Modifier.padding(
            top = 16.dp,
            bottom = 16.dp
        ),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp),
        elevation = 8.dp
    ) {
        Column {
            when (nftProps.assetType) {
                is Model3d -> {
                    Model3dViewer(nftProps)
                }
                is Image -> {
                    ImageViewer(loading, nftProps)
                }
            }
            Text(
                text = nftProps.name,
                modifier = Modifier
                    .padding(16.dp)
                    .placeholder(
                        visible = loading,
                        color = ShimmerColor,
                        highlight = PlaceholderHighlight.shimmer(White)
                    )
            )
        }

    }
}

@Composable
fun ImageViewer(
    loading: Boolean,
    nftProps: NftViewProps
) {
    Image(
        painter = rememberGlidePainter(
            request = nftProps.assetUrl
        ),
        contentDescription = "Nft Image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(top = 24.dp)
            .placeholder(
                visible = loading,
                color = ShimmerColor,
                highlight = PlaceholderHighlight.shimmer(White)
            )
    )
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

    AndroidView({ context ->
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
    }, modifier = Modifier.pointerInteropFilter {
        modelViewer?.onTouchEvent(it)
        true
    })
}