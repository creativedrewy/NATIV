
package com.creativedrewy.nativ.activity

import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.filament.ModelViewer
import com.creativedrewy.nativ.filament.scenes
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.MainViewModel
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.google.android.filament.Engine
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()

    companion object {
        init { Utils.init() }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NATIVTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    //Greeting(viewModel.viewState)
                    FilamentRoot(viewModel.viewState)
                }
            }
        }

        viewModel.loadNfts()
    }
}

@Composable
fun Greeting(
    viewState: LiveData<List<String>>
) {
    val nfts by viewState.observeAsState(listOf())

    LazyColumn {
        items(nfts) { nft ->
            SampleLabel(name = nft)
        }
    }
}

@Composable
fun SampleLabel(
    name: String
) {
    Text(text = "Hello $name!")
}

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

    SideEffect {
        val (engine, scene, asset) = scenes["car"]!!
        modelViewer?.scene = scene

        Log.v("SOL", "::: You are doing a side effect!! :;")
    }

    DisposableEffect(modelViewer) {
        onDispose {
            //TODO: Look into proper disposing of this
            //modelViewer?.destroy()
        }
    }

    AndroidView({ context ->
        LayoutInflater.from(context).inflate(
            R.layout.filament_host, FrameLayout(context), false
        ).apply {

            modelViewer = ModelViewer(
                context,
                Engine.create(),
                this as SurfaceView,
                nftProp.mediaBytes
            )
            //setupModelViewer(modelViewer)
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NATIVTheme {
        //Greeting("Android")
    }
}