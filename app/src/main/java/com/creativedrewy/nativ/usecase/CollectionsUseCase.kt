package com.creativedrewy.nativ.usecase

import com.creativedrewy.solananft.database.DatabaseRepository
import javax.inject.Inject

const val ASSORTED_COLLECTION_ID = "__assorted__"

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
     * Collections with only 1 NFT are aggregated into an "Assorted" group.
     */
    suspend fun loadCollections(): List<CollectionDisplayInfo> {
        val summaries = nftDatabaseRepository.getCollectionSummaries()

        val multiItemCollections = summaries.filter { it.nftCount > 1 }
        val singleItemCollections = summaries.filter { it.nftCount == 1 }

        val result = mutableListOf<CollectionDisplayInfo>()

        // Map multi-item collections normally
        multiItemCollections.forEach { summary ->
            val previewAsset = nftDatabaseRepository.getFirstAssetForCollection(summary.collectionId)
            val imageUrl = previewAsset?.content?.links?.get("image") ?: ""

            result.add(
                CollectionDisplayInfo(
                    collectionId = summary.collectionId,
                    collectionName = summary.collectionName
                        ?: previewAsset?.content?.metadata?.name
                        ?: (summary.collectionId.take(8) + "..."),
                    previewImageUrl = imageUrl,
                    nftCount = summary.nftCount
                )
            )
        }

        // Aggregate single-item collections into one "Assorted" group
        if (singleItemCollections.isNotEmpty()) {
            val firstSingle = singleItemCollections.first()
            val previewAsset = nftDatabaseRepository.getFirstAssetForCollection(firstSingle.collectionId)
            val imageUrl = previewAsset?.content?.links?.get("image") ?: ""

            result.add(
                CollectionDisplayInfo(
                    collectionId = ASSORTED_COLLECTION_ID,
                    collectionName = "Assorted",
                    previewImageUrl = imageUrl,
                    nftCount = singleItemCollections.sumOf { it.nftCount }
                )
            )
        }

        return result
    }

    /**
     * Search collections by matching NFT names or collection names.
     * Returns collection IDs that match, which can then be used to filter the full collection list.
     */
    suspend fun searchCollections(query: String): List<String> {
        if (query.isBlank()) return emptyList()

        val matchingIds = nftDatabaseRepository.searchCollectionIds(query)

        // Check if any matching IDs belong to single-item collections
        val summaries = nftDatabaseRepository.getCollectionSummaries()
        val singleItemIds = summaries.filter { it.nftCount == 1 }.map { it.collectionId }.toSet()

        val result = mutableListOf<String>()

        matchingIds.forEach { id ->
            if (id in singleItemIds) {
                // This match is in the assorted group
                if (ASSORTED_COLLECTION_ID !in result) {
                    result.add(ASSORTED_COLLECTION_ID)
                }
            } else {
                result.add(id)
            }
        }

        return result
    }
}
