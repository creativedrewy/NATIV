package com.creativedrewy.nativ.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.LightPurple
import com.creativedrewy.nativ.viewmodel.Image
import com.creativedrewy.nativ.viewmodel.Loading
import com.creativedrewy.nativ.viewmodel.Model3d
import com.creativedrewy.nativ.viewmodel.NftGalleryViewModel
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
        }
    )

    val viewState by viewModel.viewState.collectAsState()
    val isLoading = viewState is Loading

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 340f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Box {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 64.dp
                ),
            painter = painterResource(
                id = R.drawable.stars_bg
            ),
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
                .offset(
                    y = if (isLoading) animatedOffset.dp else 0.dp
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp),
                painter = painterResource(
                    id = R.drawable.sunset
                ),
                contentScale = ContentScale.FillWidth,
                contentDescription = ""
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(284.dp)
                .background(MaterialTheme.colors.primary)
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 64.dp
                )
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                painter = painterResource(
                    id = R.drawable.perspective_grid
                ),
                contentScale = ContentScale.FillHeight,
                contentDescription = ""
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = 64.dp
                )
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = viewState is Loading),
                onRefresh = { viewModel.reloadNfts() },
                indicator = { state, trigger ->
                    LineSwipeRefreshIndicator(
                        swipeRefreshState = state,
                        triggerDistance = trigger,
                        lineColor = HotPink.copy(alpha = 0.6f)
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp
                        )
                        .fillMaxSize()
                        .align(Alignment.TopStart)
                ) {
                    items(viewState.listItems) { nft ->
                        GalleryItemCard(
                            nftProps = nft
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun GalleryItemCard(
    nftProps: NftViewProps
) {
    Surface(
        modifier = Modifier.padding(
            top = 16.dp,
            bottom = 16.dp
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(CardDarkBlue)
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        border = BorderStroke(2.dp, HotPink),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                when (nftProps.assetType) {
                    is Model3d -> {
                        Model3dViewer(nftProps)
                    }
                    is Image -> {
                        ImageViewer(nftProps)
                    }
                }
            }
            Text(
                modifier = Modifier
                    .padding(
                        top = 8.dp
                    ),
                text = nftProps.name,
                style = MaterialTheme.typography.h5
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedCircleImage(
                    imageRes = nftProps.blockchain.logoRes,
                    size = 48.dp,
                    outlineWidth = 0.dp,
                    backgroundColor = LightPurple
                )
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
