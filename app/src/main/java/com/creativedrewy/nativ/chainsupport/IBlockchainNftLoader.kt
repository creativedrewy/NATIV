package com.creativedrewy.nativ.chainsupport

import com.creativedrewy.nativ.nft.NftMetadata

interface IBlockchainNftLoader {
    suspend fun loadNftsForAddress(address: String): List<NftMetadata>
}