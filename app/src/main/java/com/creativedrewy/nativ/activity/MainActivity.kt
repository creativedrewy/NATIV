
package com.creativedrewy.nativ.activity

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
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.filament.*
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.MainViewModel
import com.google.android.filament.*
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.MaterialProvider
import com.google.android.filament.gltfio.ResourceLoader
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var engine: Engine
    private lateinit var assetLoader: AssetLoader
    private lateinit var resourceLoader: ResourceLoader

    private lateinit var indirectLight: IndirectLight
    private lateinit var skybox: Skybox
    private var light: Int = 0

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
                    FilamentRoot()
                }
            }
        }

        //viewModel.loadNfts()
        initFilament()
    }

    override fun onDestroy() {
        super.onDestroy()

        engine.lightManager.destroy(light)
        engine.destroyEntity(light)
        engine.destroyIndirectLight(indirectLight)
        engine.destroySkybox(skybox)

        scenes.forEach {
            engine.destroyScene(it.value.scene)
            assetLoader.destroyAsset(it.value.asset)
        }

        assetLoader.destroy()
        resourceLoader.destroy()

        engine.destroy()
    }

    private fun initFilament() {
        engine = Engine.create()
        assetLoader = AssetLoader(engine, MaterialProvider(engine), EntityManager.get())
        resourceLoader = ResourceLoader(engine)

        val ibl = "courtyard_8k"
        readCompressedAsset(this, "${ibl}_ibl.ktx").let {
            indirectLight = KtxLoader.createIndirectLight(engine, it)
            indirectLight.intensity = 30_000.0f
        }

        readCompressedAsset(this, "${ibl}_skybox.ktx").let {
            skybox = KtxLoader.createSkybox(engine, it)
        }

        light = EntityManager.get().create()
        val (r, g, b) = Colors.cct(6_000.0f)
        LightManager.Builder(LightManager.Type.SUN)
            .color(r, g, b)
            .intensity(70_000.0f)
            .direction(0.28f, -0.6f, -0.76f)
            .build(engine, light)

        createScene("car", "material_car_paint.glb")
    }

    private fun createScene(name: String, gltf: String) {
        val scene = engine.createScene()
        val asset = readCompressedAsset(this, gltf).let {
            val asset = loadModelGlb(assetLoader, resourceLoader, it)
            //transformToUnitCube(engine, asset)
            asset
        }
        scene.indirectLight = indirectLight
        scene.skybox = skybox

        scene.addEntities(asset.entities)

        scene.addEntity(light)

        scenes[name] = ProductScene(engine, scene, asset)
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
fun FilamentRoot() {
    Surface(
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 16.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp),
        elevation = 8.dp
    ) {
        FilamentViewer()
    }
}

@Composable
fun FilamentViewer() {
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
    }

    AndroidView({ context ->
        LayoutInflater.from(context).inflate(
            R.layout.filament_host, FrameLayout(context), false
        ).apply {
            val (engine) = scenes["car"]!!

            modelViewer = ModelViewer(engine, this as SurfaceView).also {
                setupModelViewer(it)
            }
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