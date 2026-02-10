package com.creativedrewy.nativ.usecase

import com.creativedrewy.nativ.R
import javax.inject.Inject

class WallpaperCatalogUseCase @Inject constructor() {

    fun getAvailableWallpapers(): List<WallpaperDefinition> {
        return listOf(
            WallpaperDefinition(
                name = "NFT Gallery",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 1,
                purchaseId = "wallpaper_nft_gallery"
            ),
            WallpaperDefinition(
                name = "Cosmic Grid",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 3,
                purchaseId = "wallpaper_cosmic_grid"
            ),
            WallpaperDefinition(
                name = "Sunset Dreams",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 5,
                purchaseId = "wallpaper_sunset_dreams"
            )
        )
    }
}
