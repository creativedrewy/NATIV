package com.creativedrewy.solananft.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

data class CollectionSummary(
    val collectionId: String,
    val collectionName: String?,
    val nftCount: Int
)

@Dao
interface DasAssetDao {

    @Query("SELECT * FROM DasAssetEntity")
    fun getAssetsForOwner(): List<DasAssetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(assets: List<DasAssetEntity>)

    @Query("SELECT id FROM DasAssetEntity")
    fun getAllIds(): List<String>

    @Query("""
        SELECT collectionId, collectionName, COUNT(*) as nftCount
        FROM DasAssetEntity
        WHERE collectionId IS NOT NULL
        GROUP BY collectionId
        ORDER BY collectionName ASC
    """)
    fun getCollectionSummaries(): List<CollectionSummary>

    @Query("SELECT * FROM DasAssetEntity WHERE collectionId = :collectionId LIMIT 1")
    fun getFirstAssetForCollection(collectionId: String): DasAssetEntity?

    @Query("SELECT * FROM DasAssetEntity WHERE collectionId = :collectionId ORDER BY name ASC")
    fun getAssetsByCollectionId(collectionId: String): List<DasAssetEntity>

    @Query("SELECT * FROM DasAssetEntity WHERE id = :assetId")
    fun getAssetById(assetId: String): DasAssetEntity?

    @Query("""
        SELECT DISTINCT collectionId FROM DasAssetEntity 
        WHERE collectionId IS NOT NULL 
        AND (name LIKE '%' || :query || '%' OR collectionName LIKE '%' || :query || '%')
    """)
    fun searchCollectionIds(query: String): List<String>

    @Query("UPDATE DasAssetEntity SET collectionName = :name WHERE collectionId = :collectionId")
    fun updateCollectionName(collectionId: String, name: String)

    @Query("SELECT DISTINCT collectionId FROM DasAssetEntity WHERE collectionId IS NOT NULL AND collectionName IS NULL")
    fun getCollectionIdsWithoutName(): List<String>

    @Query("DELETE FROM DasAssetEntity")
    fun deleteAll()
}
