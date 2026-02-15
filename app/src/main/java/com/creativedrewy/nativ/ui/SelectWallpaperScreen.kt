package com.creativedrewy.nativ.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.creativedrewy.nativ.R
import com.creativedrewy.nativ.ui.theme.CardDarkBlue
import com.creativedrewy.nativ.ui.theme.TitleGray
import com.creativedrewy.nativ.viewmodel.SelectWallpaperViewModel
import com.creativedrewy.nativ.viewmodel.WallpaperViewProps

@Composable
fun SelectWallpaperScreen(
    viewModel: SelectWallpaperViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadWallpapers()
    }

    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    Box {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp),
            painter = painterResource(id = R.drawable.stars_bg),
            contentScale = ContentScale.FillHeight,
            contentDescription = ""
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Select Wallpaper",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewState.wallpapers) { wallpaper ->
                    WallpaperPreviewCard(
                        wallpaper = wallpaper,
                        onClick = {
                            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                            intent.putExtra(
                                WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                                ComponentName(context, wallpaper.serviceClass)
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WallpaperPreviewCard(
    wallpaper: WallpaperViewProps,
    onClick: () -> Unit
) {
    val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Surface(
        modifier = Modifier.then(
            if (wallpaper.isEnabled) Modifier.clickable { onClick() } else Modifier
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(CardDarkBlue)
                .padding(8.dp)
        ) {
            // 9:16 phone-shaped preview box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(9f / 16f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardDarkBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = wallpaper.previewImageRes),
                    contentDescription = wallpaper.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (!wallpaper.isEnabled) Modifier.alpha(0.4f) else Modifier),
                    contentScale = ContentScale.Crop,
                    colorFilter = if (!wallpaper.isEnabled) ColorFilter.colorMatrix(grayscaleMatrix) else null
                )
            }

            // Wallpaper name
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = wallpaper.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Medium
            )

            // Required favorites
            Text(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .fillMaxWidth(),
                text = "${wallpaper.requiredFavorites} favorite${if (wallpaper.requiredFavorites != 1) "s" else ""} required",
                maxLines = 1,
                fontSize = 12.sp,
                color = TitleGray
            )
        }
    }
}
