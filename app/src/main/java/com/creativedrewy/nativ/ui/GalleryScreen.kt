package com.creativedrewy.nativ.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.*
import com.creativedrewy.nativ.viewmodel.Loading
import com.creativedrewy.nativ.viewmodel.NftGalleryViewModel
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.creativedrewy.nativ.viewmodel.PropsWithMedia
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@ExperimentalComposeUiApi
@Composable
fun GalleryList(
    viewModel: NftGalleryViewModel = hiltViewModel(),
    onDetailsNavigate: (String) -> Unit,
    listState: LazyListState
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
                    state = listState,
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
                            nftProps = nft,
                            onDetailsNavigate = onDetailsNavigate
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
    nftProps: NftViewProps,
    onDetailsNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(
                top = 16.dp,
                bottom = 16.dp
            )
            .clickable {
                onDetailsNavigate(
                    nftProps.id.toString()
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(CardDarkBlue)
                .padding(24.dp)
        ) {
            AssetViewer(
                nftProps = PropsWithMedia(nftProps),
                outlineColor = HotPink,
                imageOnlyMode = true,
                isLoading = nftProps.isPending
            )
            Text(
                modifier = Modifier
                    .padding(
                        top = 8.dp
                    )
                    .fillMaxWidth()
                    .placeholder(
                        visible = nftProps.isPending,
                        color = DarkBlue,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = ShimmerBlue,
                        ),
                    ),
                text = nftProps.name,
                style = MaterialTheme.typography.h5
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        top = 8.dp
                    ),
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
