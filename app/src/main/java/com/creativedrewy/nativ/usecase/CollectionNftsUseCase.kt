package com.creativedrewy.nativ.usecase

import com.creativedrewy.solananft.das.DasAsset
import com.creativedrewy.solananft.database.DatabaseRepository
import javax.inject.Inject

data class NftDisplayInfo(
    val assetId: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val animationUrl: String,
    val externalUrl: String,
    val collectionId: String?,
    val collectionName: String?,
)

class CollectionNftsUseCase @Inject constructor(
    private val nftDatabaseRepository: DatabaseRepository
) {

    /**
     * Load all NFTs belonging to a specific collection.
     */
    suspend fun loadNftsForCollection(collectionId: String): List<NftDisplayInfo> {
        val assets = nftDatabaseRepository.getAssetsByCollectionId(collectionId)
        return assets.map { it.toNftDisplayInfo() }
    }

    /**
     * Load a single NFT by its asset ID.
     */
    suspend fun loadNft(assetId: String): NftDisplayInfo? {
        val asset = nftDatabaseRepository.getAssetById(assetId)
        return asset?.toNftDisplayInfo()
    }

    /**
     * Get the collection name for a given collection ID.
     */
    suspend fun getCollectionName(collectionId: String): String? {
        val firstAsset = nftDatabaseRepository.getFirstAssetForCollection(collectionId)
        // The collection name is denormalized on each NFT row
        return firstAsset?.grouping
            ?.firstOrNull { it.groupKey == "collection" }
            ?.let {
                // Query the database for the collection name stored on the entity
                val summaries = nftDatabaseRepository.getCollectionSummaries()
                summaries.firstOrNull { s -> s.collectionId == collectionId }?.collectionName
            }
    }

    private fun DasAsset.toNftDisplayInfo(): NftDisplayInfo {
        return NftDisplayInfo(
            assetId = id,
            name = content?.metadata?.name ?: "",
            description = content?.metadata?.description ?: "",
            imageUrl = content?.links?.get("image") ?: "",
            animationUrl = content?.links?.get("animation_url") ?: "",
            externalUrl = content?.links?.get("external_url") ?: "",
            collectionId = grouping?.firstOrNull { it.groupKey == "collection" }?.groupValue,
            collectionName = null, // Will be filled from DB if needed
        )
    }
}
