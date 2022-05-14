package com.creativedrewy.nativ.ethereumnft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

    override suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>> {
        TODO("Not yet implemented")
    }
}
