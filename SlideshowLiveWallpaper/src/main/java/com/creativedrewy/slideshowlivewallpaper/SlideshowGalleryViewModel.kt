package com.creativedrewy.slideshowlivewallpaper

import com.creativedrewy.sharedui.WallpaperFavoritesUseCase
import com.creativedrewy.solananft.viewmodel.NftViewProps
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SlideshowGalleryViewModel @Inject constructor(
    private val wallpaperFavoritesUseCase: WallpaperFavoritesUseCase
) {

    val wallpaperItems: Flow<List<NftViewProps>>
        get() = wallpaperFavoritesUseCase.wallpaperItems

}
