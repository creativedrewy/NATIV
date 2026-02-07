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
    val fileTypes: List<String> = emptyList(),
)

class CollectionNftsUseCase @Inject constructor(
    private val nftDatabaseRepository: DatabaseRepository
) {

    /**
     * Load all NFTs belonging to a specific collection.
     * For the assorted group, loads all NFTs from single-item collections.
     */
    suspend fun loadNftsForCollection(collectionId: String): List<NftDisplayInfo> {
        val assets = if (collectionId == ASSORTED_COLLECTION_ID) {
            nftDatabaseRepository.getAssetsFromSingleItemCollections()
        } else {
            nftDatabaseRepository.getAssetsByCollectionId(collectionId)
        }
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
        if (collectionId == ASSORTED_COLLECTION_ID) return "Assorted"

        val summaries = nftDatabaseRepository.getCollectionSummaries()
        val collectionName = summaries.firstOrNull { it.collectionId == collectionId }?.collectionName

        if (collectionName != null) return collectionName

        // Fall back to the first NFT's name if no collection name is available
        val firstAsset = nftDatabaseRepository.getFirstAssetForCollection(collectionId)
        return firstAsset?.content?.metadata?.name
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
            fileTypes = content?.files?.mapNotNull { it.type } ?: emptyList(),
        )
    }
}
