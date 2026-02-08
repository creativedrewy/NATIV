package com.creativedrewy.nativ.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteNftDao {

    @Query("SELECT * FROM FavoriteNft")
    fun getAll(): List<FavoriteNft>

    @Query("SELECT * FROM FavoriteNft WHERE tokenAddress = :tokenAddress")
    fun getFavorite(tokenAddress: String): FavoriteNft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(nft: FavoriteNft)

    @Query("DELETE FROM FavoriteNft WHERE tokenAddress = :tokenAddress")
    fun delete(tokenAddress: String)
}
