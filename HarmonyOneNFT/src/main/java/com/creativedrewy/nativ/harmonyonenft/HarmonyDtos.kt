package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.nft.NftMetadata

data class HarmonyNftResultDto(
    val ownerAddress: String?,
    val tokenAddress: String?,
    val tokenID: String?,
    val tokenURI: String?,
    val meta: NftMetadata?
)