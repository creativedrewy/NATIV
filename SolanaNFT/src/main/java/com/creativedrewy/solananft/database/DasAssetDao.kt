package com.creativedrewy.solananft.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DasAssetDao {

    @Query("SELECT * FROM DasAssetEntity")
    fun getAssetsForOwner(): List<DasAssetEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(assets: List<DasAssetEntity>)

    @Query("SELECT id FROM DasAssetEntity")
    fun getAllIds(): List<String>
}
