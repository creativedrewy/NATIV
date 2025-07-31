package com.creativedrewy.solananft.metaplex

import android.util.Log
import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.LoaderNftResult
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.*
import com.creativedrewy.solananft.accounts.AccountRepository
import com.creativedrewy.solananft.database.DatabaseRepository
import com.creativedrewy.solananft.das.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import com.solana.core.PublicKey
import javax.inject.Inject

class MetaplexNftUseCase @Inject constructor(
    private val accountsRepository: AccountRepository,
    private val databaseRepository: DatabaseRepository
) : IBlockchainNftLoader {

    /**
     * Load and emit the full set of *possible* NFTs, then emit each NFT's metadata as it is loaded
     */
    override suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult> = flow {
        val statusMap = mutableMapOf<String, NftMetaStatus>()

        // Load cached
        val cachedAssets = databaseRepository.getCachedAssetsForOwner(address)

        cachedAssets.forEach { asset ->
            val uri = asset.content?.jsonUri ?: return@forEach
            statusMap[uri] = MetaLoaded(mapToNftMetadata(asset))
        }

        if (statusMap.isNotEmpty()) {
            emit(LoaderNftResult(chain, statusMap))
        }

//        try {
//            val fetchedAssets = accountsRepository.getAssetsByOwner(PublicKey(address))
//
//            databaseRepository.cacheNewAssets(fetchedAssets)
//
//            val cachedIds = cachedAssets.map { it.id }.toSet()
//            val newAssets = fetchedAssets.filter { it.id !in cachedIds }
//
//            newAssets.forEach { asset ->
//                val uri = asset.content?.jsonUri ?: return@forEach
//                statusMap[uri] = Pending
//            }
//            if (newAssets.isNotEmpty()) {
//                emit(LoaderNftResult(chain, statusMap))
//            }
//
//            newAssets.forEach { asset ->
//                val uri = asset.content?.jsonUri ?: return@forEach
//
//                try {
//                    val nftMeta = mapToNftMetadata(asset)
//                    statusMap[uri] = MetaLoaded(nftMeta)
//                    emit(LoaderNftResult(chain, statusMap))
//                } catch (e: Exception) {
//                    Log.e("SOL", "Error mapping DAS asset to metadata for $uri", e)
//                    statusMap[uri] = Invalid
//                }
//            }
//
//            emit(LoaderNftResult(chain, statusMap))
//        } catch (e: Exception) {
//            Log.e("SOL", "Error attempting to load nfts for address $address", e)
//            emit(LoaderNftResult(chain, emptyMap()))
//        }
    }

    private fun mapToNftMetadata(asset: DasAsset): NftMetadata {
        val content = asset.content ?: throw IllegalArgumentException("Asset has no content")
        val meta = content.metadata

        val attributes = meta.attributes?.map {
            NftAttributes(it.traitType, it.value, 0)
        } ?: emptyList()

        val category = if (content.files?.any { it.type == NftFileTypes.GLB } == true) {
            NftCategories.VR
        } else {
            NftCategories.Image
        }

        val properties = NftProperties(
            category = category,
            files = content.files?.map { FileDetails(it.uri, it.type) },
            creators = asset.creators?.map { NftCreator(it.address) }
        )

        return NftMetadata(
            meta.name,
            meta.symbol,
            meta.description,
            content.links?.get("image"),
            content.links?.get("animation_url"),
            content.links?.get("external_url"),
            attributes,
            properties
        )
    }
}
