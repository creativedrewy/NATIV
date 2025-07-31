package com.creativedrewy.nativ.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.*
import com.creativedrewy.nativ.viewmodel.Blockchain
import com.creativedrewy.nativ.viewmodel.Image
import com.creativedrewy.nativ.viewmodel.Loading
import com.creativedrewy.nativ.viewmodel.NftGalleryViewModel
import com.creativedrewy.nativ.viewmodel.NftViewProps
import com.creativedrewy.nativ.viewmodel.PropsWithMedia
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.util.UUID

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.max

@ExperimentalComposeUiApi
@Composable
fun GalleryList(
    viewModel: NftGalleryViewModel = hiltViewModel(),
    onDetailsNavigate: (String) -> Unit,
    listState: LazyGridState
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNfts()
        }
    )

    val viewState by viewModel.viewState.collectAsState()
    val isLoading = viewState is Loading

    GalleryListContent(
        listItems = viewState.listItems,
        isRefreshing = isLoading,
        onRefresh = { viewModel.reloadNfts() },
        onDetailsNavigate = onDetailsNavigate,
        listState = listState
    )
}

@ExperimentalComposeUiApi
@Composable
fun GalleryItemCard(
    nftProps: NftViewProps,
    onDetailsNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
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
                .padding(8.dp)
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
                    .basicMarquee(
                        initialDelayMillis = 2000
                    )
                    .placeholder(
                        visible = nftProps.isPending,
                        color = DarkBlue,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = ShimmerBlue,
                        ),
                    ),
                text = nftProps.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun GalleryListContent(
    listItems: List<NftViewProps>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDetailsNavigate: (String) -> Unit,
    listState: LazyGridState
) {
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
                    y = if (isRefreshing) animatedOffset.dp else 0.dp
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
                state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
                onRefresh = onRefresh,
                indicator = { state, trigger ->
                    LineSwipeRefreshIndicator(
                        swipeRefreshState = state,
                        triggerDistance = trigger,
                        lineColor = HotPink.copy(alpha = 0.6f)
                    )
                }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.TopStart),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listItems.size) { index ->
                        val nft = listItems[index]
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
@Preview
@Composable
fun GalleryListPreview() {
    NATIVTheme {
        GalleryListContent(
            listItems = listOf(
                NftViewProps(
                    id = UUID.randomUUID(),
                    name = "Preview NFT",
                    description = "Description",
                    blockchain = Blockchain(
                        ticker = "SOL",
                        logoRes = -1
                    ),
                    displayImageUrl = "https://example.com/image.png",
                    assetType = Image,
                    assetUrl = "https://example.com/image.png",
                    isPending = false
                ),
                NftViewProps(
                    id = UUID.randomUUID(),
                    name = "Sample NFT 2",
                    description = "Another description",
                    blockchain = Blockchain(
                        ticker = "ETH",
                        logoRes = -1
                    ),
                    displayImageUrl = "https://example.com/image2.png",
                    assetType = Image,
                    assetUrl = "https://example.com/image2.png",
                    isPending = false
                ),
                NftViewProps(
                    id = UUID.randomUUID(),
                    name = "Sample NFT 3",
                    description = "Yet another description",
                    blockchain = Blockchain(
                        ticker = "BTC",
                        logoRes = -1
                    ),
                    displayImageUrl = "https://example.com/image3.png",
                    assetType = Image,
                    assetUrl = "https://example.com/image3.png",
                    isPending = false
                )
            ),
            isRefreshing = false,
            onRefresh = {},
            onDetailsNavigate = {},
            listState = rememberLazyGridState()
        )
    }
}
