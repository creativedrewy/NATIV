package com.creativedrewy.nativ.harmonyonenft

import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.LoaderNftResult
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.MetaLoaded
import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class HarmonyNftUseCase @Inject constructor(
    private val harmonyNftRepository: HarmonyNftRepository
): IBlockchainNftLoader {

    override suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult> = flow {
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
        //emit(metaMap)

        val erc1155Dtos = withContext(Dispatchers.IO) {
            harmonyNftRepository.getErc155Nfts(sanitizedAddr)
        }

        erc1155Dtos.filter { it.meta != null && it.tokenURI != null }
            .forEach {
                metaMap[it.tokenURI!!] = MetaLoaded(it.meta!!)
            }

        //Emit all loaded NFTs
        //emit(metaMap)
    }
}