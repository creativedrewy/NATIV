package com.creativedrewy.nativ.usecase

import com.creativedrewy.nativ.R
import com.creativedrewy.slideshowlivewallpaper.SlideshowWallpaperService
import com.creativedrewy.verticalgridlivewallpaper.VerticalGridWallpaperService
import javax.inject.Inject

class WallpaperCatalogUseCase @Inject constructor() {

    fun getAvailableWallpapers(): List<WallpaperDefinition> {
        return listOf(
            WallpaperDefinition(
                name = "Baroque",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 1,
                purchaseId = "wallpaper_nft_gallery",
                serviceClass = SlideshowWallpaperService::class.java
            ),
            WallpaperDefinition(
                name = "The Grid",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 8,
                purchaseId = "wallpaper_cosmic_grid",
                serviceClass = VerticalGridWallpaperService::class.java
            ),
            WallpaperDefinition(
                name = "Fullscreen",
                previewImageRes = R.drawable.sunset,
                requiredFavorites = 1,
                purchaseId = "wallpaper_full_screen",
                serviceClass = SlideshowWallpaperService::class.java
            )
        )
    }
}
