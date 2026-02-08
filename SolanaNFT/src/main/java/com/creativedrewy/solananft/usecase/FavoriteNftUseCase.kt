package com.creativedrewy.solananft.usecase

import com.creativedrewy.solananft.database.FavoriteNft
import com.creativedrewy.solananft.repository.FavoritesRepository
import javax.inject.Inject

class FavoriteNftUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val assetDownloadUseCase: AssetDownloadUseCase
) {

    /**
     * Toggle the favorite status of an NFT.
     * When favoriting, downloads and caches the media (images or GLB files, not videos).
     *
     * @return true if the NFT is now favorited, false if unfavorited
     */
    suspend fun toggleFavorite(
        tokenAddress: String,
        name: String,
        imageUrl: String,
        mediaUrl: String,
        assetType: String
    ): Boolean {
        val currentlyFavorited = favoritesRepository.isFavorited(tokenAddress)

        if (currentlyFavorited) {
            favoritesRepository.removeFavorite(tokenAddress)
            return false
        } else {
            val nft = FavoriteNft(
                tokenAddress = tokenAddress,
                name = name,
                imageUrl = imageUrl,
                mediaUrl = mediaUrl,
                assetType = assetType
            )
            favoritesRepository.addFavorite(nft)

            // Download and cache media for offline use (images and GLB files, not videos)
            if (mediaUrl.isNotBlank()) {
                val bytes = assetDownloadUseCase.downloadAsset(mediaUrl)
                if (bytes.isNotEmpty()) {
                    favoritesRepository.cacheMediaFile(tokenAddress, bytes)
                }
            }

            return true
        }
    }

    suspend fun isFavorited(tokenAddress: String): Boolean {
        return favoritesRepository.isFavorited(tokenAddress)
    }

    suspend fun getAllFavorites(): List<FavoriteNft> {
        return favoritesRepository.getAllFavorites()
    }
}
