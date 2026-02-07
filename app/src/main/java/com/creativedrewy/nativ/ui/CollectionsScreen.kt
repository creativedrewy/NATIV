package com.creativedrewy.nativ.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.ui.theme.DarkBlue
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Lexend
import com.creativedrewy.nativ.ui.theme.TitleGray
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.CollectionViewProps
import com.creativedrewy.nativ.viewmodel.CollectionsViewModel
import com.creativedrewy.nativ.viewmodel.CollectionsViewState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@ExperimentalComposeUiApi
@Composable
fun CollectionsScreen(
    viewModel: CollectionsViewModel = hiltViewModel(),
    onCollectionNavigate: (String) -> Unit,
    listState: LazyGridState
) {
    LaunchedEffect(Unit) {
        viewModel.loadCollections()
    }

    val viewState by viewModel.viewState.collectAsState()

    CollectionsContent(
        viewState = viewState,
        onRefresh = { viewModel.reloadCollections() },
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onCollectionNavigate = onCollectionNavigate,
        listState = listState
    )
}

@ExperimentalComposeUiApi
@Composable
fun CollectionsContent(
    viewState: CollectionsViewState,
    onRefresh: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onCollectionNavigate: (String) -> Unit,
    listState: LazyGridState
) {
    val isLoading = viewState is CollectionsViewState.Loading
    val collections = when (viewState) {
        is CollectionsViewState.Display -> viewState.collections
        else -> emptyList()
    }
    val searchQuery = when (viewState) {
        is CollectionsViewState.Display -> viewState.searchQuery
        else -> ""
    }

    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 340f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000),
            repeatMode = RepeatMode.Restart
        )
    )

    Box {
        // Background layers (matching existing gallery style)
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
                .fillMaxSize()
                .align(Alignment.TopStart)
                .offset(y = if (isLoading) animatedOffset.dp else 0.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                modifier = Modifier
                    .width(320.dp)
                    .height(320.dp),
                painter = painterResource(id = R.drawable.sunset),
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

        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = onRefresh,
                indicator = { state, trigger ->
                    LineSwipeRefreshIndicator(
                        swipeRefreshState = state,
                        triggerDistance = trigger,
                        lineColor = HotPink.copy(alpha = 0.6f)
                    )
                }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Search bar
                    SearchBar(
                        query = searchQuery,
                        onQueryChanged = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    )

                    // Collections grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(collections.size) { index ->
                            val collection = collections[index]
                            CollectionCard(
                                collection = collection,
                                onClick = { onCollectionNavigate(collection.collectionId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(query) }

    // Sync external query changes
    LaunchedEffect(query) {
        if (text != query) text = query
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = CardDarkBlue,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = Turquoise.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    onQueryChanged(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = Lexend
                ),
                singleLine = true,
                cursorBrush = SolidColor(Turquoise),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            text = "Search NFTs and collections...",
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 14.sp,
                            fontFamily = Lexend
                        )
                    }
                    innerTextField()
                }
            )
            if (text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Clear search",
                    tint = Turquoise.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            text = ""
                            onQueryChanged("")
                        }
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun CollectionCard(
    collection: CollectionViewProps,
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
            // Preview image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                if (collection.previewImageUrl.isNotBlank()) {
                    val imageRequest = remember(collection.previewImageUrl) {
                        ImageRequest.Builder(context)
                            .data(collection.previewImageUrl)
                            .build()
                    }

                    SubcomposeAsyncImage(
                        model = imageRequest,
                        contentDescription = collection.collectionName,
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

            // Collection name
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .basicMarquee(initialDelayMillis = 2000),
                text = collection.collectionName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium
            )

            // NFT count
            Text(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth(),
                text = "${collection.nftCount} NFT${if (collection.nftCount != 1) "s" else ""}",
                maxLines = 1,
                style = MaterialTheme.typography.caption,
                color = TitleGray
            )
        }
    }
}
