package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.nft.NftMetadata

data class HarmonyNftResultDto(
    val ownerAddress: String? = null,
    val tokenAddress: String? = null,
    val tokenID: String? = null,
    val tokenURI: String? = null,
    val meta: NftMetadata? = null
)