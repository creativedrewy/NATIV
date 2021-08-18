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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.viewmodel.Image
import com.creativedrewy.nativ.viewmodel.Model3d
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.google.accompanist.glide.rememberGlidePainter
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer

@ExperimentalComposeUiApi
@Composable
fun AssetViewer(
    nftProps: NftViewProps,
    outlineColor: Color,
    imageOnlyMode: Boolean = false
) {
    Box(
        modifier = Modifier
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
        contentScale = ContentScale.FillWidth,
        painter = rememberGlidePainter(
            request = nftProps.assetUrl
        ),
        contentDescription = "Nft Image",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
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

//                            var transformMatrix = FloatArray(16)
//                            transformMatrix = tcm.getTransform(tcm.getInstance(gltfAsset.root), transformMatrix)
//
//                            val animAppend = (animValue.animatedValue as Float) - lastAnimValue
//
//                            Matrix.rotateM(transformMatrix, 0, animAppend, 0f, 1.0f, 0f)
//                            tcm.setTransform(tcm.getInstance(gltfAsset.root), transformMatrix)
//
//                            lastAnimValue = animValue.animatedValue as Float

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
        }
    )
}