package com.creativedrewy.nativ.usecase

import com.creativedrewy.fullscreengallerywallpaper.FullScreenGalleryWallpaperService
import com.creativedrewy.slideshowlivewallpaper.R
import com.creativedrewy.slideshowlivewallpaper.SlideshowWallpaperService
import com.creativedrewy.verticalgridlivewallpaper.VerticalGridWallpaperService
import javax.inject.Inject

class WallpaperCatalogUseCase @Inject constructor() {

    fun getAvailableWallpapers(): List<WallpaperDefinition> {
        return listOf(
            WallpaperDefinition(
                name = "Baroque",
                previewImageRes = R.drawable.baroque_preview,
                requiredFavorites = 1,
                description = "Treat your NFTs like royalty",
                purchaseId = "wallpaper_baroque",
                serviceClass = SlideshowWallpaperService::class.java
            ),
            WallpaperDefinition(
                name = "Lattice",
                previewImageRes = com.creativedrewy.verticalgridlivewallpaper.R.drawable.lattice_preview,
                requiredFavorites = 8,
                description = "When you want to see your favorites all at once",
                purchaseId = "wallpaper_lattice",
                serviceClass = VerticalGridWallpaperService::class.java
            ),
            WallpaperDefinition(
                name = "Immersive Canvas",
                previewImageRes = com.creativedrewy.fullscreengallerywallpaper.R.drawable.imersive_preview,
                requiredFavorites = 1,
                description = "A classic wallpaper experience evolved",
                purchaseId = "wallpaper_immersive_canvas",
                serviceClass = FullScreenGalleryWallpaperService::class.java
            )
        )
    }
}
