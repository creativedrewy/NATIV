package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import javax.inject.Inject

class HarmonyNftUseCase @Inject constructor(

): IBlockchainNftLoader {

    override suspend fun loadNftsForAddress(address: String): List<NftMetadata> {
        //val dtos = withContext(Dispatchers.IO) {
        //  openSeaRepository.getNftsForAddress(address)
        //}

        return listOf()
    }
}