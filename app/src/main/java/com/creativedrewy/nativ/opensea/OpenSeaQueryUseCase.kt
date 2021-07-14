package com.creativedrewy.nativ.opensea

import com.creativedrewy.nativ.nft.NftMetadata
import javax.inject.Inject

class OpenSeaQueryUseCase @Inject constructor(

) {

    suspend fun getOpenSeaNftsByAddress(address: String): List<NftMetadata> {
        return listOf()
    }
}