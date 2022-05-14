package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import kotlinx.coroutines.flow.Flow

interface IBlockchainNftLoader {
    suspend fun loadNftsForAddress(address: String): List<NftMetadata>

    suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>>
}
