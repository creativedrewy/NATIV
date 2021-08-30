package com.creativedrewy.nativ.ethereumnft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.NftAttributes
import com.creativedrewy.nativ.chainsupport.nft.NftCategories
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import com.creativedrewy.nativ.chainsupport.nft.NftProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OpenSeaQueryUseCase @Inject constructor(
    private val openSeaRepository: OpenSeaRepository
) : IBlockchainNftLoader {

    override suspend fun loadNftsForAddress(address: String): List<NftMetadata> {
        val dtos = withContext(Dispatchers.IO) {
            openSeaRepository.getNftsForAddress(address)
        }

        val nftSpecResults = dtos.assets.map { asset ->
            NftMetadata(
                name = asset.name.orEmpty(),
                symbol = "",
                description = asset.description.orEmpty(),
                image = asset.image_preview_url.orEmpty(),
                animationUrl = asset.animation_url.orEmpty(),
                externalUrl = asset.external_link.orEmpty(),
                attributes = asset.traits.orEmpty().map {
                    NftAttributes(
                        traitType = it.trait_type,
                        value = it.value
                    )
                },
                properties = NftProperties(NftCategories.Image, listOf(), listOf())
            )
        }

        return nftSpecResults
    }
}
