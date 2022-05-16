package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import kotlinx.coroutines.flow.Flow

interface IBlockchainNftLoader {
    suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>>
}
