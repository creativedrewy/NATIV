package com.creativedrewy.nativ.opensea

import com.creativedrewy.nativ.nft.NftMetadata
import com.creativedrewy.nativ.nft.NftProperties
import javax.inject.Inject

class OpenSeaQueryUseCase @Inject constructor(
    private val openSeaRepository: OpenSeaRepository
) {

    suspend fun getOpenSeaNftsByAddress(address: String): List<NftMetadata> {
        val dtos = openSeaRepository.getNftsForAddress(address)

        val nftSpecResults = dtos.assets.map { asset ->
            NftMetadata(
                name = asset.name.orEmpty(),
                symbol = "",
                description = asset.description.orEmpty(),
                image = asset.image_preview_url.orEmpty(),
                animationUrl = asset.animation_url.orEmpty(),
                externalUrl = asset.external_link.orEmpty(),
                properties = NftProperties("image", listOf(), listOf())
            )
        }

        return nftSpecResults
    }
}