package com.creativedrewy.verticalgridlivewallpaper

import com.creativedrewy.sharedui.WallpaperItemsViewModel
import com.creativedrewy.solananft.viewmodel.NftViewProps
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerticalGridViewModel @Inject constructor(
    private val wallpaperItemsViewModel: WallpaperItemsViewModel
) {

    val wallpaperItems: Flow<List<NftViewProps>>
        get() = wallpaperItemsViewModel.wallpaperItems

    fun isVideoItem(item: NftViewProps): Boolean {
        return wallpaperItemsViewModel.isVideoItem(item)
    }
}
