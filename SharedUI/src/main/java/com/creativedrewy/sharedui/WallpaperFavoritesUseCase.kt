package com.creativedrewy.sharedui

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


class WallpaperFavoritesUseCase @Inject constructor(
    favoriteNftUseCase: FavoriteNftUseCase,
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
                ) ?: NftViewProps(
                    id = UUID.nameUUIDFromBytes(fav.tokenAddress.toByteArray()),
                    name = fav.name,
                    displayImageUrl = fav.imageUrl,
                    isPending = false
                )
            }
        }

}
