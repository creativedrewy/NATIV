package com.creativedrewy.nativ.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.*
import com.creativedrewy.nativ.viewmodel.DetailsViewModel
import com.creativedrewy.nativ.viewmodel.Ready
import com.google.accompanist.flowlayout.FlowRow
import java.util.*

@ExperimentalComposeUiApi
@Composable
fun DetailsScreen(
    nftId: String,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(
        key1 = Unit,
        block = {
            viewModel.loadNftDetails(nftId)
        }
    )

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
                val loadedNft = (viewState as Ready).props

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
                                nftProps = loadedNft,
                                outlineColor = Turquoise
                            )
                        }
                    }
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
                            bottom = 16.dp
                        )
                        .verticalScroll(
                            state = scrollState
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    top = 8.dp
                                ),
                            text = loadedNft.name,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.onPrimary
                        )
                        OutlinedCircleImage(
                            imageRes = loadedNft.blockchain.logoRes,
                            size = 48.dp,
                            outlineWidth = 0.dp,
                            backgroundColor = LightPurple
                        )
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
                                        .padding(8.dp)
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
    }
}
