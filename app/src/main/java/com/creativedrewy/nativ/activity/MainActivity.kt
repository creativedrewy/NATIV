
package com.creativedrewy.nativ.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.MainViewModel
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import java.nio.ByteBuffer

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()

    companion object {
        init { Utils.init() }
    }

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NATIVTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    FilamentRoot(viewModel.viewState)
                }
            }
        }

        viewModel.loadNfts()
    }
}

fun readCompressedAsset(context: Context, assetName: String): ByteBuffer {
    val input = context.assets.open(assetName)
    val bytes = ByteArray(input.available())
    input.read(bytes)
    return ByteBuffer.wrap(bytes)
}


@ExperimentalComposeUiApi
@Composable
fun FilamentRoot(
    viewState: LiveData<List<NftViewProps>>
) {
    val nfts by viewState.observeAsState(listOf())

    LazyColumn {
        items(nfts) { nft ->
            Surface(
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
                shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp),
                elevation = 8.dp
            ) {
                FilamentViewer(nft)
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun FilamentViewer(
    nftProp: NftViewProps
) {
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