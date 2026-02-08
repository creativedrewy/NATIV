package com.creativedrewy.nativ.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.HotPink
import com.creativedrewy.nativ.ui.theme.Lexend
import com.creativedrewy.nativ.ui.theme.LightPurple
import com.creativedrewy.nativ.ui.theme.TitleGray
import com.creativedrewy.nativ.ui.theme.Turquoise
import com.creativedrewy.nativ.viewmodel.DetailsViewModel
import com.creativedrewy.nativ.viewmodel.Ready
import com.google.accompanist.flowlayout.FlowRow
import java.util.Locale

@ExperimentalComposeUiApi
@Composable
fun DetailsScreen(
    nftId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNftDetails(nftId)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val viewState by viewModel.viewState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.primaryVariant)
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight(0.75f),
            painter = painterResource(
                id = R.drawable.stars_bg_variant
            ),
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            if (viewState is Ready) {
                val state = viewState as Ready
                val propsWithMedia = state.item
                val loadedNft = state.item.props

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            painter = painterResource(
                                id = R.drawable.perspective_grid_variant
                            ),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = ""
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .aspectRatio(1f)
                                .background(Color.Transparent)
                        ) {
                            AssetViewer(
                                nftProps = propsWithMedia,
                                outlineColor = Turquoise,
                                imageOnlyMode = state.isLoadingAsset,
                                alpha = if (state.isLoadingAsset) 0.5f else 1f
                            )
                        }

                        if (state.isLoadingAsset) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center),
                                color = Turquoise
                            )
                        }
                    }
                }

                // Favorite star button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (state.isFavorited) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (state.isFavorited) "Unfavorite" else "Favorite",
                        tint = if (state.isFavorited) Turquoise else Turquoise.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { viewModel.toggleFavorite() }
                    )
                }

                val uriHandler = LocalUriHandler.current

                val annotatedString = buildAnnotatedString {
                    append(loadedNft.siteUrl)
                    addStyle(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Lexend,
                            color = Turquoise,
                            fontSize = 14.sp
                        ),
                        start = 0,
                        end = loadedNft.siteUrl.length
                    )
                    addStringAnnotation(
                        tag = "link",
                        annotation = loadedNft.siteUrl,
                        start = 0,
                        end = loadedNft.siteUrl.length
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                        )
                        .verticalScroll(
                            state = scrollState
                        )
                ) {
                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val (title, logo) = createRefs()

                        Text(
                            modifier = Modifier
                                .constrainAs(title) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                }
                                .padding(
                                    top = 8.dp,
                                    end = 48.dp
                                ),
                            text = loadedNft.name,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.onPrimary
                        )
                        if (loadedNft.blockchain.logoRes > 0) {
                            Box(
                                modifier = Modifier
                                    .constrainAs(logo) {
                                        top.linkTo(parent.top)
                                        end.linkTo(parent.end)
                                    }
                                    .width(48.dp)
                                    .padding(
                                        top = 8.dp
                                    )
                            ) {
                                OutlinedCircleImage(
                                    imageRes = loadedNft.blockchain.logoRes,
                                    size = 48.dp,
                                    outlineWidth = 0.dp,
                                    backgroundColor = LightPurple
                                )
                            }
                        }
                    }
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 8.dp
                            ),
                        text = loadedNft.description,
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onPrimary
                    )
                    ClickableText(
                        modifier = Modifier
                            .padding(
                                top = 8.dp
                            ),
                        text = annotatedString,
                        onClick = { position ->
                            val annotations = annotatedString.getStringAnnotations(
                                tag = "link",
                                start = position,
                                end = position
                            )
                            annotations.firstOrNull()?.let {
                                uriHandler.openUri(it.item)
                            }
                        },
                        style = MaterialTheme.typography.body2,
                    )
                    Text(
                        modifier = Modifier
                            .padding(
                                top = 8.dp
                            ),
                        text = "Attributes",
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.colors.onPrimary
                    )

                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                bottom = 16.dp
                            )
                    ) {
                        loadedNft.attributes.forEach { attrib ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        border = BorderStroke(2.dp, HotPink),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = attrib.name.uppercase(Locale.getDefault()),
                                        color = TitleGray
                                    )
                                    Text(
                                        text = attrib.value,
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Snackbar host overlay
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) { data ->
            Snackbar(
                backgroundColor = HotPink,
                contentColor = Color.White,
                snackbarData = data
            )
        }
    }
}
