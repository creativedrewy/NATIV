package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.chainsupport.nft.NftMetadata

interface IBlockchainNftLoader {
    suspend fun loadNftsForAddress(address: String): List<NftMetadata>
}
