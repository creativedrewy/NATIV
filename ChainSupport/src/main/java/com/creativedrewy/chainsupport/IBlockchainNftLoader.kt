package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import kotlinx.coroutines.flow.Flow

data class LoaderNftResult(
    val supportedChain: SupportedChain,
    val metaMap: Map<String, NftMetaStatus>,
    val pageInfo: LoaderPageInfo? = null
)
data class LoaderPageInfo(
    val page: Int,
    val itemCount: Int,
    val isLastPage: Boolean
)

interface IBlockchainNftLoader {
    suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult>
}
