package com.creativedrewy.nativ.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.ui.theme.DarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.CollectionDetailViewModel
import com.creativedrewy.nativ.viewmodel.CollectionDetailViewState
import com.creativedrewy.nativ.viewmodel.CollectionNftViewProps

@ExperimentalComposeUiApi
@Composable
fun CollectionDetailScreen(
    collectionId: String,
    viewModel: CollectionDetailViewModel = hiltViewModel(),
    onNftNavigate: (String) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(collectionId) {
        viewModel.loadCollection(collectionId)
    }

    val viewState by viewModel.viewState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        // Background
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            painter = painterResource(id = R.drawable.stars_bg),
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(284.dp)
                .background(MaterialTheme.colors.primary)
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                painter = painterResource(id = R.drawable.perspective_grid),
                contentScale = ContentScale.FillHeight,
                contentDescription = ""
            )
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            // Header with back button and collection name
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Turquoise
                    )
                }

                val title = when (viewState) {
                    is CollectionDetailViewState.Display ->
                        (viewState as CollectionDetailViewState.Display).collectionName
                    else -> "Loading..."
                }

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onPrimary
                )
            }

            when (viewState) {
                is CollectionDetailViewState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Turquoise)
                    }
                }
                is CollectionDetailViewState.Display -> {
                    val displayState = viewState as CollectionDetailViewState.Display
                    val listState = rememberLazyGridState()

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(displayState.nfts.size) { index ->
                            val nft = displayState.nfts[index]
                            NftCard(
                                nft = nft,
                                onClick = { onNftNavigate(nft.assetId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun NftCard(
    nft: CollectionNftViewProps,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(CardDarkBlue)
                .padding(8.dp)
        ) {
            // NFT image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (nft.imageUrl.isNotBlank()) {
                    val imageRequest = remember(nft.imageUrl) {
                        ImageRequest.Builder(context)
                            .data(nft.imageUrl)
                            .build()
                    }

                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = nft.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = HotPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.h4,
                            color = Turquoise
                        )
                    }
                }
            }

            // NFT name
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .basicMarquee(initialDelayMillis = 2000),
                text = nft.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}
