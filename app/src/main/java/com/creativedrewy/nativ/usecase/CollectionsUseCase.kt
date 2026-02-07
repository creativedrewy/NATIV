package com.creativedrewy.nativ.usecase

import com.creativedrewy.solananft.database.DatabaseRepository
import javax.inject.Inject

data class CollectionDisplayInfo(
    val collectionId: String,
    val collectionName: String,
    val previewImageUrl: String,
    val nftCount: Int
)

class CollectionsUseCase @Inject constructor(
    private val nftDatabaseRepository: DatabaseRepository
) {

    /**
     * Load all collection summaries with preview images.
     */
    suspend fun loadCollections(): List<CollectionDisplayInfo> {
        val summaries = nftDatabaseRepository.getCollectionSummaries()

        return summaries.map { summary ->
            val previewAsset = nftDatabaseRepository.getFirstAssetForCollection(summary.collectionId)
            val imageUrl = previewAsset?.content?.links?.get("image") ?: ""

            CollectionDisplayInfo(
                collectionId = summary.collectionId,
                collectionName = summary.collectionName ?: (summary.collectionId.take(8) + "..."),
                previewImageUrl = imageUrl,
                nftCount = summary.nftCount
            )
        }
    }

    /**
     * Search collections by matching NFT names or collection names.
     * Returns collection IDs that match, which can then be used to filter the full collection list.
     */
    suspend fun searchCollections(query: String): List<String> {
        if (query.isBlank()) return emptyList()
        return nftDatabaseRepository.searchCollectionIds(query)
    }
}
