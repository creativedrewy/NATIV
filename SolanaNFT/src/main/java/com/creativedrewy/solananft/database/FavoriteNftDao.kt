package com.creativedrewy.solananft.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteNftDao {

    @Query("SELECT * FROM FavoriteNft ORDER BY sortOrder ASC")
    fun getAll(): List<FavoriteNft>

    @Query("SELECT * FROM FavoriteNft ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<FavoriteNft>>

    @Query("SELECT * FROM FavoriteNft WHERE tokenAddress = :tokenAddress")
    fun getFavorite(tokenAddress: String): FavoriteNft?

    @Query("SELECT MAX(sortOrder) FROM FavoriteNft")
    fun getMaxSortOrder(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nft: FavoriteNft)

    @Query("UPDATE FavoriteNft SET sortOrder = :sortOrder WHERE tokenAddress = :tokenAddress")
    fun updateSortOrder(tokenAddress: String, sortOrder: Int)

    @Query("DELETE FROM FavoriteNft WHERE tokenAddress = :tokenAddress")
    fun delete(tokenAddress: String)
}
