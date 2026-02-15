package com.creativedrewy.solananft.usecase

import android.util.Log
import com.creativedrewy.nativ.chainsupport.IBlockchainNftLoader
import com.creativedrewy.nativ.chainsupport.LoaderNftResult
import com.creativedrewy.nativ.chainsupport.SupportedChain
import com.creativedrewy.nativ.chainsupport.nft.Invalid
import com.creativedrewy.nativ.chainsupport.nft.MetaLoaded
import com.creativedrewy.nativ.chainsupport.nft.NftMetaStatus
import com.creativedrewy.nativ.chainsupport.nft.Pending
import com.creativedrewy.solananft.repository.AccountRepository
import com.creativedrewy.solananft.repository.NftAssetRepository
import com.solana.core.PublicKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MetaplexNftUseCase @Inject constructor(
    private val accountsRepository: AccountRepository,
    private val databaseRepository: NftAssetRepository,
    private val nftMetadataMapper: NftMetadataMapper
) : IBlockchainNftLoader {

    /**
     * Load and emit the full set of *possible* NFTs, then emit each NFT's metadata as it is loaded
     */
    override suspend fun loadNftsThenMetaForAddress(chain: SupportedChain, address: String): Flow<LoaderNftResult> =
        flow {
            val statusMap = mutableMapOf<String, NftMetaStatus>()

            // Load cached
            val cachedAssets = databaseRepository.getCachedAssetsForOwner(address)

            cachedAssets.forEach { asset ->
                val uri = asset.content?.jsonUri ?: return@forEach
                statusMap[uri] = MetaLoaded(nftMetadataMapper.mapToNftMetadata(asset))
            }

            if (statusMap.isNotEmpty()) {
                emit(LoaderNftResult(chain, statusMap))
            }

            try {
                val fetchedAssets = accountsRepository.getAssetsByOwner(PublicKey(address))

                databaseRepository.cacheNewAssets(fetchedAssets)

                // Resolve collection names for any collections that don't have one yet
                resolveCollectionNames()

                val cachedIds = cachedAssets.map { it.id }.toSet()
                val newAssets = fetchedAssets.filter { it.id !in cachedIds }

                newAssets.forEach { asset ->
                    val uri = asset.content?.jsonUri ?: return@forEach
                    statusMap[uri] = Pending
                }
                if (newAssets.isNotEmpty()) {
                    emit(LoaderNftResult(chain, statusMap))
                }

                newAssets.forEach { asset ->
                    val uri = asset.content?.jsonUri ?: return@forEach

                    try {
                        val nftMeta = nftMetadataMapper.mapToNftMetadata(asset)
                        statusMap[uri] = MetaLoaded(nftMeta)
                        emit(LoaderNftResult(chain, statusMap))
                    } catch (e: Exception) {
                        Log.e("SOL", "Error mapping DAS asset to metadata for $uri", e)
                        statusMap[uri] = Invalid
                    }
                }

                emit(LoaderNftResult(chain, statusMap))
            } catch (e: Exception) {
                Log.e("SOL", "Error attempting to load nfts for address $address", e)
                emit(LoaderNftResult(chain, emptyMap()))
            }
        }

    /**
     * For each collection that doesn't have a resolved name yet,
     * fetch the collection asset metadata via getAsset and update the database.
     */
    private suspend fun resolveCollectionNames() {
        try {
            val unnamedCollections = databaseRepository.getCollectionIdsWithoutName()

            unnamedCollections.forEach { collectionId ->
                try {
                    val collectionAsset = accountsRepository.getAsset(collectionId)
                    val name = collectionAsset?.content?.metadata?.name

                    if (!name.isNullOrBlank()) {
                        databaseRepository.updateCollectionName(collectionId, name)
                    }
                } catch (e: Exception) {
                    Log.e("SOL", "Error resolving collection name for $collectionId", e)
                }
            }
        } catch (e: Exception) {
            Log.e("SOL", "Error resolving collection names", e)
        }
    }
}
