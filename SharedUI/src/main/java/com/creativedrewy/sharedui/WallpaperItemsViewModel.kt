package com.creativedrewy.sharedui

import com.creativedrewy.nativ.chainsupport.nft.ImageAndVideo
import com.creativedrewy.nativ.chainsupport.nft.isMp4Url
import com.creativedrewy.solananft.R
import com.creativedrewy.solananft.repository.NftAssetRepository
import com.creativedrewy.solananft.usecase.FavoriteNftUseCase
import com.creativedrewy.solananft.usecase.NftMetadataMapper
import com.creativedrewy.solananft.viewmodel.Blockchain
import com.creativedrewy.solananft.viewmodel.NftViewProps
import com.creativedrewy.solananft.viewmodel.toNftViewProps
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

/**
 * Shared view-model-like class that both wallpaper services can inject.
 * Provides the list of favorited NFTs as [NftViewProps] and a helper to
 * detect video items.
 */
class WallpaperItemsViewModel @Inject constructor(
    private val favoriteNftUseCase: FavoriteNftUseCase,
    private val nftAssetRepository: NftAssetRepository,
    private val nftMetadataMapper: NftMetadataMapper
) {

    val wallpaperItems: Flow<List<NftViewProps>> = favoriteNftUseCase.observeFavorites()
        .map { favorites ->
            favorites.map { fav ->
                val metadata = nftMetadataMapper.loadNftMetadataById(fav.tokenAddress, nftAssetRepository)

                metadata?.toNftViewProps(
                    assetId = fav.tokenAddress,
                    blockchain = Blockchain(
                        ticker = "SOL",
                        logoRes = R.drawable.solana_logo
                    )
                )
                    ?: NftViewProps(
                        id = UUID.nameUUIDFromBytes(fav.tokenAddress.toByteArray()),
                        name = fav.name,
                        displayImageUrl = fav.imageUrl,
                        isPending = false
                    )
            }
        }

    fun isVideoItem(item: NftViewProps): Boolean {
        if (item.assetType is ImageAndVideo) return true
        return isMp4Url(item.videoUrl)
    }
}
