package com.creativedrewy.solananft.database

import com.creativedrewy.solananft.das.DasAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val dao: DasAssetDao
) {

    suspend fun getCachedAssetsForOwner(address: String): List<DasAsset> =
        withContext(Dispatchers.IO) {
            dao.getAssetsForOwner().map { it.toDasAsset() }
        }

    suspend fun cacheNewAssets(assets: List<DasAsset>) = withContext(Dispatchers.IO) {
        // Use REPLACE strategy so updated grouping/collection data is always persisted
        dao.insertAll(assets.map { it.toEntity() })
    }

    suspend fun getCollectionSummaries(): List<CollectionSummary> =
        withContext(Dispatchers.IO) {
            dao.getCollectionSummaries()
        }

    suspend fun getFirstAssetForCollection(collectionId: String): DasAsset? =
        withContext(Dispatchers.IO) {
            dao.getFirstAssetForCollection(collectionId)?.toDasAsset()
        }

    suspend fun getAssetsByCollectionId(collectionId: String): List<DasAsset> =
        withContext(Dispatchers.IO) {
            dao.getAssetsByCollectionId(collectionId).map { it.toDasAsset() }
        }

    suspend fun getAssetById(assetId: String): DasAsset? =
        withContext(Dispatchers.IO) {
            dao.getAssetById(assetId)?.toDasAsset()
        }

    suspend fun searchCollectionIds(query: String): List<String> =
        withContext(Dispatchers.IO) {
            dao.searchCollectionIds(query)
        }

    suspend fun getCollectionIdsWithoutName(): List<String> =
        withContext(Dispatchers.IO) {
            dao.getCollectionIdsWithoutName()
        }

    suspend fun updateCollectionName(collectionId: String, name: String) =
        withContext(Dispatchers.IO) {
            dao.updateCollectionName(collectionId, name)
        }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        dao.deleteAll()
    }
}
