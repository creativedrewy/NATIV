package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class HarmonyNftUseCase @Inject constructor(
    private val harmonyNftRepository: HarmonyNftRepository
): IBlockchainNftLoader {

    override suspend fun loadNftsForAddress(address: String): List<NftMetadata> {
        val sanitizedAddr = address.lowercase(Locale.getDefault())

        val erc721Dtos = withContext(Dispatchers.IO) {
            harmonyNftRepository.getErc721Nfts(sanitizedAddr)
        }

        val erc1155Dtos = withContext(Dispatchers.IO) {
            harmonyNftRepository.getErc155Nfts(sanitizedAddr)
        }

        return (erc721Dtos + erc1155Dtos)
            .filter { it.meta != null }
            .map { it.meta!! }
    }

    override suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>> {
        TODO("Not yet implemented")
    }
}