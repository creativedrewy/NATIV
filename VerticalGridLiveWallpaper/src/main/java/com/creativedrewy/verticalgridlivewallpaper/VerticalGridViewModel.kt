package com.creativedrewy.verticalgridlivewallpaper

import com.creativedrewy.sharedui.WallpaperFavoritesUseCase
import com.creativedrewy.solananft.viewmodel.NftViewProps
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerticalGridViewModel @Inject constructor(
    private val wallpaperFavoritesUseCase: WallpaperFavoritesUseCase
) {

    val wallpaperItems: Flow<List<NftViewProps>>
        get() = wallpaperFavoritesUseCase.wallpaperItems
}
