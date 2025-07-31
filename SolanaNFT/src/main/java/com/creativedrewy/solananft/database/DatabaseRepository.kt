package com.creativedrewy.solananft.database

import com.creativedrewy.solananft.das.DasAsset
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ViewModelScoped
class DatabaseRepository @Inject constructor(
    private val dao: DasAssetDao
) {

    suspend fun getCachedAssetsForOwner(address: String): List<DasAsset> =
        withContext(Dispatchers.IO) {
            dao.getAssetsForOwner().map { it.toDasAsset() }
        }

    suspend fun cacheNewAssets(assets: List<DasAsset>) = withContext(Dispatchers.IO) {
        val existingIds = dao.getAllIds().toSet()

        val newAssets = assets.filter { it.id !in existingIds }

        dao.insertAll(newAssets.map { it.toEntity() })
    }
}
