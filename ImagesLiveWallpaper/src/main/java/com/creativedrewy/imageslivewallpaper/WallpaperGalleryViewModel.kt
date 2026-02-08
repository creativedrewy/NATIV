package com.creativedrewy.imageslivewallpaper

import com.creativedrewy.solananft.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class WallpaperNftItem(
    val tokenAddress: String,
    val name: String,
    val imageUrl: String
)

class WallpaperGalleryViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    val wallpaperItems: Flow<List<WallpaperNftItem>> = favoritesRepository.observeAllFavorites()
        .map { favorites ->
            favorites.map { nft ->
                WallpaperNftItem(
                    tokenAddress = nft.tokenAddress,
                    name = nft.name,
                    imageUrl = nft.imageUrl
                )
            }
        }
}
