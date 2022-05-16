package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.nft.MetaLoaded
import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import com.creativedrewy.nativ.chainsupport.nft.NftMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class HarmonyNftUseCase @Inject constructor(
    private val harmonyNftRepository: HarmonyNftRepository
): IBlockchainNftLoader {

    override suspend fun loadNftsForAddress(address: String): List<NftMetadata> {
        return listOf()
    }

    override suspend fun loadNftsThenMetaForAddress(address: String): Flow<Map<String, NftMetaStatus>> = flow {
        val sanitizedAddr = address.lowercase(Locale.getDefault())
        val metaMap = mutableMapOf<String, NftMetaStatus>()

        val erc721Dtos = withContext(Dispatchers.IO) {
            harmonyNftRepository.getErc721Nfts(sanitizedAddr)
        }

        erc721Dtos.filter { it.meta != null && it.tokenURI != null }
            .forEach {
                metaMap[it.tokenURI!!] = MetaLoaded(it.meta!!)
            }

        //Emit the ERC721 NFTs
        emit(metaMap)

        val erc1155Dtos = withContext(Dispatchers.IO) {
            harmonyNftRepository.getErc155Nfts(sanitizedAddr)
        }

        erc1155Dtos.filter { it.meta != null && it.tokenURI != null }
            .forEach {
                metaMap[it.tokenURI!!] = MetaLoaded(it.meta!!)
            }

        //Emit all loaded NFTs
        emit(metaMap)
    }
}